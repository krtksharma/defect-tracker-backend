package com.cognizant.dto;

import java.time.LocalDateTime;

public class CommentDTO {

    private Integer       id;
    private Integer       defectId;
    private String        author;
    private String        authorRole;
    private String        content;
    private LocalDateTime createdAt;

    public CommentDTO() {}

    // Getters & Setters
    public Integer       getId()                     { return id; }
    public void          setId(Integer id)           { this.id = id; }

    public Integer       getDefectId()               { return defectId; }
    public void          setDefectId(Integer d)      { this.defectId = d; }

    public String        getAuthor()                 { return author; }
    public void          setAuthor(String a)         { this.author = a; }

    public String        getAuthorRole()             { return authorRole; }
    public void          setAuthorRole(String r)     { this.authorRole = r; }

    public String        getContent()                { return content; }
    public void          setContent(String c)        { this.content = c; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void          setCreatedAt(LocalDateTime t) { this.createdAt = t; }
}
