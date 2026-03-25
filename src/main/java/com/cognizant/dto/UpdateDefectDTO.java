package com.cognizant.dto;

import java.util.List;

// Extended UpdateDefectDTO — includes who made the change (for audit log)
public class UpdateDefectDTO {
    private Integer              id;
    private String               status;
    private List<UpdateResolutionDTO> resolutions;

    // NEW — who is making this change (from JWT on frontend)
    private String updatedBy;
    private String updatedByRole;

    public UpdateDefectDTO() {}

    public Integer   getId()                             { return id; }
    public void      setId(Integer id)                   { this.id = id; }
    public String    getStatus()                         { return status; }
    public void      setStatus(String s)                 { this.status = s; }
    public List<UpdateResolutionDTO> getResolutions()    { return resolutions; }
    public void      setResolutions(List<UpdateResolutionDTO> r) { this.resolutions = r; }
    public String    getUpdatedBy()                      { return updatedBy; }
    public void      setUpdatedBy(String u)              { this.updatedBy = u; }
    public String    getUpdatedByRole()                  { return updatedByRole; }
    public void      setUpdatedByRole(String r)          { this.updatedByRole = r; }
}
