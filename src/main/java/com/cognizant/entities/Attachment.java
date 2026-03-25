package com.cognizant.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

// Stores metadata about uploaded files (screenshots, logs, etc.)
// The actual file is saved on disk; we only store the filename + path here.
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Original filename e.g. "screenshot.png"
    @Column(name = "original_name", nullable = false)
    private String originalName;

    // Stored filename on disk e.g. "1234_screenshot.png" (unique)
    @Column(name = "stored_name", nullable = false, unique = true)
    private String storedName;

    // MIME type e.g. "image/png", "application/pdf"
    @Column(name = "file_type", length = 50)
    private String fileType;

    // File size in bytes
    @Column(name = "file_size")
    private Long fileSize;

    // Who uploaded it
    @Column(name = "uploaded_by", length = 50)
    private String uploadedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "defect_id", nullable = false)
    @JsonBackReference
    private Defect defect;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer       getId()                         { return id; }
    public void          setId(Integer id)               { this.id = id; }

    public String        getOriginalName()               { return originalName; }
    public void          setOriginalName(String n)       { this.originalName = n; }

    public String        getStoredName()                 { return storedName; }
    public void          setStoredName(String n)         { this.storedName = n; }

    public String        getFileType()                   { return fileType; }
    public void          setFileType(String t)           { this.fileType = t; }

    public Long          getFileSize()                   { return fileSize; }
    public void          setFileSize(Long s)             { this.fileSize = s; }

    public String        getUploadedBy()                 { return uploadedBy; }
    public void          setUploadedBy(String u)         { this.uploadedBy = u; }

    public LocalDateTime getUploadedAt()                 { return uploadedAt; }
    public void          setUploadedAt(LocalDateTime t)  { this.uploadedAt = t; }

    public Defect        getDefect()                     { return defect; }
    public void          setDefect(Defect d)             { this.defect = d; }
}
