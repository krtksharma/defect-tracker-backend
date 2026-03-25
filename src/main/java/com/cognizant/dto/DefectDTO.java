package com.cognizant.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DefectDTO {

    private Integer          id;
    private String           title;
    private String           defectdetails;
    private String           stepstoreproduce;
    private String           priority;
    private LocalDate        detectedon;
    private LocalDate        expectedresolution;
    private String           reportedbytesterid;
    private String           assignedtodeveloperid;
    private String           severity;
    private String           status;
    private int              projectcode;
    private List<ResolutionDTO> resolutions;

    // ── NEW ──────────────────────────────────────────────────────────
    private String           expectedbehavior;
    private String           actualbehavior;
    // ─────────────────────────────────────────────────────────────────

    public DefectDTO() {}

    // Getters & Setters
    public Integer   getId()                              { return id; }
    public void      setId(Integer id)                    { this.id = id; }
    public String    getTitle()                           { return title; }
    public void      setTitle(String t)                   { this.title = t; }
    public String    getDefectdetails()                   { return defectdetails; }
    public void      setDefectdetails(String d)           { this.defectdetails = d; }
    public String    getStepstoreproduce()                { return stepstoreproduce; }
    public void      setStepstoreproduce(String s)        { this.stepstoreproduce = s; }
    public String    getPriority()                        { return priority; }
    public void      setPriority(String p)                { this.priority = p; }
    public LocalDate getDetectedon()                      { return detectedon; }
    public void      setDetectedon(LocalDate d)           { this.detectedon = d; }
    public LocalDate getExpectedresolution()              { return expectedresolution; }
    public void      setExpectedresolution(LocalDate e)   { this.expectedresolution = e; }
    public String    getReportedbytesterid()              { return reportedbytesterid; }
    public void      setReportedbytesterid(String r)      { this.reportedbytesterid = r; }
    public String    getAssignedtodeveloperid()           { return assignedtodeveloperid; }
    public void      setAssignedtodeveloperid(String a)   { this.assignedtodeveloperid = a; }
    public String    getSeverity()                        { return severity; }
    public void      setSeverity(String s)                { this.severity = s; }
    public String    getStatus()                          { return status; }
    public void      setStatus(String s)                  { this.status = s; }
    public int       getProjectcode()                     { return projectcode; }
    public void      setProjectcode(int p)                { this.projectcode = p; }
    public List<ResolutionDTO> getResolutions()           { return resolutions; }
    public void      setResolutions(List<ResolutionDTO> r){ this.resolutions = r; }
    public String    getExpectedbehavior()                { return expectedbehavior; }
    public void      setExpectedbehavior(String e)        { this.expectedbehavior = e; }
    public String    getActualbehavior()                  { return actualbehavior; }
    public void      setActualbehavior(String a)          { this.actualbehavior = a; }
}
