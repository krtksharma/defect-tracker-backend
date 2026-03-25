package com.cognizant.dto;

import java.time.LocalDateTime;

public class AttachmentDTO {

    private Integer       id;
    private Integer       defectId;
    private String        originalName;
    private String        storedName;
    private String        fileType;
    private Long          fileSize;
    private String        uploadedBy;
    private LocalDateTime uploadedAt;
    // URL the frontend uses to display/download the file
    private String        downloadUrl;

    public AttachmentDTO() {}

    // Getters & Setters
    public Integer       getId()                           { return id; }
    public void          setId(Integer id)                 { this.id = id; }

    public Integer       getDefectId()                     { return defectId; }
    public void          setDefectId(Integer d)            { this.defectId = d; }

    public String        getOriginalName()                 { return originalName; }
    public void          setOriginalName(String n)         { this.originalName = n; }

    public String        getStoredName()                   { return storedName; }
    public void          setStoredName(String n)           { this.storedName = n; }

    public String        getFileType()                     { return fileType; }
    public void          setFileType(String t)             { this.fileType = t; }

    public Long          getFileSize()                     { return fileSize; }
    public void          setFileSize(Long s)               { this.fileSize = s; }

    public String        getUploadedBy()                   { return uploadedBy; }
    public void          setUploadedBy(String u)           { this.uploadedBy = u; }

    public LocalDateTime getUploadedAt()                   { return uploadedAt; }
    public void          setUploadedAt(LocalDateTime t)    { this.uploadedAt = t; }

    public String        getDownloadUrl()                  { return downloadUrl; }
    public void          setDownloadUrl(String url)        { this.downloadUrl = url; }
}
