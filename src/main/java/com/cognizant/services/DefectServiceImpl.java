package com.cognizant.services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.dto.*;
import com.cognizant.entities.*;
import com.cognizant.repositries.*;

@Service("defectServiceImpl")
public class DefectServiceImpl implements DefectService {

    @Autowired private DefectEntityRepository defectRepository;
    @Autowired private BugResolutionCalculator resolutionCalculator;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private NotificationRepository notificationRepository;

    // In-memory daily limit tracker (resets on server restart — fine for demo)
    private final Map<String, Map<LocalDate, Integer>> bugsAssignedPerDeveloperPerDay = new HashMap<>();

    // ── CREATE ────────────────────────────────────────────────────────
    @Override
    public String createDefect(DefectDTO defectDTO) throws Exception {
        try {
            if (defectDTO.getStepstoreproduce() == null) {
                throw new IllegalArgumentException("Stepstoreproduce is required");
            }
            String    developerId   = defectDTO.getAssignedtodeveloperid();
            LocalDate currentDate   = LocalDate.now();

            // Enforce max 5 bugs per developer per day
            if (developerId != null && !developerId.isBlank()) {
                Map<LocalDate, Integer> developerBugs =
                        bugsAssignedPerDeveloperPerDay.computeIfAbsent(developerId, k -> new HashMap<>());
                int today = developerBugs.getOrDefault(currentDate, 0);
                if (today >= 5) {
                    return "Developer has reached the maximum bug assignment limit for today";
                }
                developerBugs.put(currentDate, today + 1);
            }

            Defect defect = convertToEntity(defectDTO);
            Defect saved  = defectRepository.save(defect);

            // Audit: bug created
            String reporter = defectDTO.getReportedbytesterid();
            auditLogRepository.save(AuditLog.bugCreated(saved, reporter, "tester"));

            // Notification: notify assigned developer
            if (developerId != null && !developerId.isBlank()) {
                auditLogRepository.save(AuditLog.assigned(saved, reporter, "tester", developerId));
                notificationRepository.save(
                        Notification.bugAssigned(developerId, saved.getId(), saved.getTitle(), reporter));
            }

            return "success";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // ── UPDATE / RESOLVE ──────────────────────────────────────────────
    @Override
    public String updateDefect(UpdateDefectDTO defectDTO) {
        Optional<Defect> optionalDefect = defectRepository.findById(defectDTO.getId());
        if (optionalDefect.isEmpty()) return "Defect not found";

        Defect defect   = optionalDefect.get();
        String oldStatus = defect.getStatus();
        String newStatus = defectDTO.getStatus();
        String updatedBy = defectDTO.getUpdatedBy() != null ? defectDTO.getUpdatedBy() : "developer";
        String updatedByRole = defectDTO.getUpdatedByRole() != null ? defectDTO.getUpdatedByRole() : "developer";

        defect.setStatus(newStatus);

        // Resolutions
        if (defectDTO.getResolutions() != null && !defectDTO.getResolutions().isEmpty()) {
            List<Resolution> resolutions = new ArrayList<>();
            for (UpdateResolutionDTO r : defectDTO.getResolutions()) {
                Resolution res = new Resolution();
                res.setDefect(defect);
                res.setResolutiondate(r.getResolutiondate());
                res.setResolution(r.getResolution());
                resolutions.add(res);

                // Audit: resolution note
                auditLogRepository.save(AuditLog.resolved(defect, updatedBy, updatedByRole, r.getResolution()));
            }
            defect.setResolutions(resolutions);
        }

        defectRepository.save(defect);

        // Audit: status change
        if (!oldStatus.equals(newStatus)) {
            auditLogRepository.save(AuditLog.statusChanged(defect, updatedBy, updatedByRole, oldStatus, newStatus));

            // Notify tester that status changed
            if (defect.getReportedbytesterid() != null) {
                notificationRepository.save(
                        Notification.statusChanged(defect.getReportedbytesterid(),
                                defect.getId(), defect.getTitle(), updatedBy, newStatus));
            }
        }

        return "Defect updated successfully";
    }

    // ── REST OF METHODS (unchanged) ───────────────────────────────────
    @Override
    public List<Defect> findDefectsByDeveloper(String developerId) {
        return defectRepository.findByAssignedtodeveloperid(developerId);
    }

    @Override
    public Optional<Defect> findDefectById(Integer id) throws Exception {
        return defectRepository.findById(id)
                .map(Optional::of)
                .orElseThrow(() -> new Exception("Defect Not Found In The Database"));
    }

    @Override
    public DefectReportDTO generateDefectReport(int projectId) throws Exception {
        List<Defect> defects = defectRepository.findByProjectcode(projectId);
        if (defects.isEmpty()) throw new Exception("Records Not Found For This Project Id");
        DefectReportDTO report = new DefectReportDTO();
        report.setProjectId(projectId);
        report.setDefects(defects.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return report;
    }

    @Override
    public List<DefectDTO> getAllDefects() {
        return ((List<Defect>) defectRepository.findAll())
                .stream().map(this::convertToDTO).toList();
    }

    // ── ENTITY ↔ DTO helpers ──────────────────────────────────────────
    private Defect convertToEntity(DefectDTO dto) {
        LocalDate expectedDate = resolutionCalculator
                .calculateExpectedResolutionDate(dto.getSeverity(), dto.getPriority(), dto.getDetectedon());

        Defect d = new Defect();
        d.setTitle(dto.getTitle());
        d.setDefectdetails(dto.getDefectdetails());
        d.setStepstoreproduce(dto.getStepstoreproduce());
        d.setPriority(dto.getPriority());
        d.setDetectedon(dto.getDetectedon());
        d.setExpectedresolution(expectedDate);
        d.setReportedbytesterid(dto.getReportedbytesterid());
        d.setAssignedtodeveloperid(dto.getAssignedtodeveloperid());
        d.setSeverity(dto.getSeverity());
        d.setStatus(dto.getStatus() != null ? dto.getStatus() : "Open");
        d.setProjectcode(dto.getProjectcode());
        d.setExpectedbehavior(dto.getExpectedbehavior());
        d.setActualbehavior(dto.getActualbehavior());

        if (dto.getResolutions() != null) {
            for (ResolutionDTO r : dto.getResolutions()) {
                Resolution res = new Resolution();
                res.setDefect(d);
                res.setResolutiondate(r.getResolutiondate());
                res.setResolution(r.getResolution());
                d.getResolutions().add(res);
            }
        }
        return d;
    }

    public DefectDTO convertToDTO(Defect d) {
        DefectDTO dto = new DefectDTO();
        dto.setId(d.getId());
        dto.setTitle(d.getTitle());
        dto.setDefectdetails(d.getDefectdetails());
        dto.setStepstoreproduce(d.getStepstoreproduce());
        dto.setPriority(d.getPriority());
        dto.setDetectedon(d.getDetectedon());
        dto.setExpectedresolution(d.getExpectedresolution());
        dto.setReportedbytesterid(d.getReportedbytesterid());
        dto.setAssignedtodeveloperid(d.getAssignedtodeveloperid());
        dto.setSeverity(d.getSeverity());
        dto.setStatus(d.getStatus());
        dto.setProjectcode(d.getProjectcode());
        dto.setExpectedbehavior(d.getExpectedbehavior());
        dto.setActualbehavior(d.getActualbehavior());
        dto.setResolutions(d.getResolutions().stream().map(r -> {
            ResolutionDTO rdto = new ResolutionDTO();
            rdto.setId(r.getId());
            rdto.setDefectId(d.getId());
            rdto.setResolutiondate(r.getResolutiondate());
            rdto.setResolution(r.getResolution());
            return rdto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
