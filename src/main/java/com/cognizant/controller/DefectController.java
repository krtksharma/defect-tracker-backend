package com.cognizant.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cognizant.dto.DefectDTO;
import com.cognizant.dto.DefectDetailsDTO;
import com.cognizant.dto.DefectReportDTO;
import com.cognizant.dto.UpdateDefectDTO;
import com.cognizant.entities.Defect;
import com.cognizant.services.DefectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
@Tag(name = "Defects Management", description = "Rest API of Defect management module")
public class DefectController {

    private final Logger logger = LoggerFactory.getLogger(DefectController.class);
    private final DefectService defectService;

    DefectController(DefectService defectService) {
        this.defectService = defectService;
    }

    @Operation(description = "Create a new defect")
    @PostMapping("defects/new")
    public ResponseEntity<?> createDefect(@Valid @RequestBody DefectDTO defectDTO) throws Exception {
        logger.info("Create defect: {}", defectDTO.getTitle());
        String result = defectService.createDefect(defectDTO);
        if (result.equals("success")) {
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\": \"Success\"}");
        } else if (result.contains("maximum bug assignment limit")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"" + result + "\"}");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + result + "\"}");
    }

    @Operation(description = "Update status and resolution of a defect")
    @PutMapping("/defects/resolve")
    public ResponseEntity<String> resolveDefect(@RequestBody UpdateDefectDTO defectDTO) throws Exception {
        logger.info("Update defect #{}", defectDTO.getId());
        String status = defectService.updateDefect(defectDTO);
        if (status.equals("Defect updated successfully")) {
            return new ResponseEntity<>("{\"message\": \"Success\"}", HttpStatus.OK);
        } else if (status.equals("Defect not found")) {
            throw new Exception("Defect Not Found Exception");
        }
        return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
    }

    @Operation(description = "Get defects assigned to a developer")
    @GetMapping("/defects/assignedto/{developerId}")
    public ResponseEntity<List<Defect>> getDefectsByDeveloper(@PathVariable String developerId) throws Exception {
        logger.info("Get defects for developer: {}", developerId);
        List<Defect> defects = defectService.findDefectsByDeveloper(developerId);
        return ResponseEntity.ok(defects != null ? defects : Collections.emptyList());
    }

    @Operation(description = "Get defect details by ID — includes expectedbehavior and actualbehavior")
    @GetMapping("/defects/{defectId}")
    public ResponseEntity<DefectDetailsDTO> getDefectDetails(@PathVariable Integer defectId) throws Exception {
        logger.info("Get defect #{}", defectId);
        Defect defect = defectService.findDefectById(defectId)
                .orElseThrow(() -> new RuntimeException("Defect not found with id " + defectId));

        DefectDetailsDTO dto = new DefectDetailsDTO();
        dto.setId(defect.getId());
        dto.setTitle(defect.getTitle());
        dto.setDefectdetails(defect.getDefectdetails());
        dto.setStepstoreproduce(defect.getStepstoreproduce());
        dto.setPriority(defect.getPriority());
        dto.setDetectedon(defect.getDetectedon());
        dto.setExpectedresolution(defect.getExpectedresolution());
        dto.setReportedbytesterid(defect.getReportedbytesterid());
        dto.setAssignedtodeveloperid(defect.getAssignedtodeveloperid());
        dto.setSeverity(defect.getSeverity());
        dto.setStatus(defect.getStatus());
        dto.setProjectcode(defect.getProjectcode());
        dto.setResolutions(defect.getResolutions());
        // ── NEW fields ──
        dto.setExpectedbehavior(defect.getExpectedbehavior());
        dto.setActualbehavior(defect.getActualbehavior());

        return ResponseEntity.ok(dto);
    }

    @Operation(description = "Generate defect report by project code")
    @GetMapping("/defects/report/{projectId}")
    public ResponseEntity<DefectReportDTO> getDefectReport(@PathVariable int projectId) throws Exception {
        logger.info("Generate report for project #{}", projectId);
        return new ResponseEntity<>(defectService.generateDefectReport(projectId), HttpStatus.OK);
    }

    @Operation(description = "Get all defects — includes expectedbehavior and actualbehavior")
    @GetMapping("/defects/getAll")
    public ResponseEntity<List<DefectDTO>> getAllDefects() {
        logger.info("Get all defects");
        return new ResponseEntity<>(defectService.getAllDefects(), HttpStatus.OK);
    }
}
