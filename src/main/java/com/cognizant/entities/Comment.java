package com.cognizant.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Who wrote the comment (userName)
    @Column(name = "author", nullable = false, length = 50)
    private String author;

    // The role of the author at time of commenting
    @Column(name = "author_role", length = 20)
    private String authorRole;

    // Comment text
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    // When it was posted
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Link to the defect — many comments belong to one defect
    @ManyToOne
    @JoinColumn(name = "defect_id", nullable = false)
    @JsonBackReference
    private Defect defect;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer       getId()                     { return id; }
    public void          setId(Integer id)           { this.id = id; }

    public String        getAuthor()                 { return author; }
    public void          setAuthor(String a)         { this.author = a; }

    public String        getAuthorRole()             { return authorRole; }
    public void          setAuthorRole(String r)     { this.authorRole = r; }

    public String        getContent()                { return content; }
    public void          setContent(String c)        { this.content = c; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void          setCreatedAt(LocalDateTime t) { this.createdAt = t; }

    public Defect        getDefect()                 { return defect; }
    public void          setDefect(Defect d)         { this.defect = d; }
}
