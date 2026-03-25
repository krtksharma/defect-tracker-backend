package com.cognizant.entities;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

/**
 * Immutable audit trail — one row per change.
 * actionType examples: STATUS_CHANGED, ASSIGNED, COMMENT_ADDED, ATTACHMENT_ADDED, BUG_CREATED, RESOLVED
 */
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "defect_id", nullable = false)
    @JsonBackReference
    private Defect defect;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;   // STATUS_CHANGED | ASSIGNED | COMMENT_ADDED | RESOLVED | BUG_CREATED | ATTACHMENT_ADDED

    @Column(name = "performed_by", nullable = false, length = 50)
    private String performedBy;  // userName

    @Column(name = "performer_role", length = 20)
    private String performerRole;

    @Column(name = "old_value", length = 200)
    private String oldValue;     // e.g. previous status "Open"

    @Column(name = "new_value", length = 200)
    private String newValue;     // e.g. new status "In Progress"

    @Column(name = "note", length = 500)
    private String note;         // human readable description

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    // Factory helpers
    public static AuditLog statusChanged(Defect d, String by, String role, String from, String to) {
        AuditLog a = new AuditLog();
        a.setDefect(d); a.setActionType("STATUS_CHANGED");
        a.setPerformedBy(by); a.setPerformerRole(role);
        a.setOldValue(from); a.setNewValue(to);
        a.setNote(by + " changed status from " + from + " to " + to);
        return a;
    }

    public static AuditLog assigned(Defect d, String by, String role, String developer) {
        AuditLog a = new AuditLog();
        a.setDefect(d); a.setActionType("ASSIGNED");
        a.setPerformedBy(by); a.setPerformerRole(role);
        a.setNewValue(developer);
        a.setNote(by + " assigned bug to " + developer);
        return a;
    }

    public static AuditLog bugCreated(Defect d, String by, String role) {
        AuditLog a = new AuditLog();
        a.setDefect(d); a.setActionType("BUG_CREATED");
        a.setPerformedBy(by); a.setPerformerRole(role);
        a.setNote(by + " created this bug");
        return a;
    }

    public static AuditLog resolved(Defect d, String by, String role, String resolution) {
        AuditLog a = new AuditLog();
        a.setDefect(d); a.setActionType("RESOLVED");
        a.setPerformedBy(by); a.setPerformerRole(role);
        a.setNewValue(resolution.length() > 100 ? resolution.substring(0, 100) + "…" : resolution);
        a.setNote(by + " added a resolution note");
        return a;
    }

    public static AuditLog commentAdded(Defect d, String by, String role) {
        AuditLog a = new AuditLog();
        a.setDefect(d); a.setActionType("COMMENT_ADDED");
        a.setPerformedBy(by); a.setPerformerRole(role);
        a.setNote(by + " commented on this bug");
        return a;
    }

    public static AuditLog attachmentAdded(Defect d, String by, String role, String filename) {
        AuditLog a = new AuditLog();
        a.setDefect(d); a.setActionType("ATTACHMENT_ADDED");
        a.setPerformedBy(by); a.setPerformerRole(role);
        a.setNewValue(filename);
        a.setNote(by + " attached " + filename);
        return a;
    }

    // Getters & Setters
    public Integer       getId()                       { return id; }
    public void          setId(Integer id)             { this.id = id; }
    public Defect        getDefect()                   { return defect; }
    public void          setDefect(Defect d)           { this.defect = d; }
    public String        getActionType()               { return actionType; }
    public void          setActionType(String t)       { this.actionType = t; }
    public String        getPerformedBy()              { return performedBy; }
    public void          setPerformedBy(String b)      { this.performedBy = b; }
    public String        getPerformerRole()            { return performerRole; }
    public void          setPerformerRole(String r)    { this.performerRole = r; }
    public String        getOldValue()                 { return oldValue; }
    public void          setOldValue(String v)         { this.oldValue = v; }
    public String        getNewValue()                 { return newValue; }
    public void          setNewValue(String v)         { this.newValue = v; }
    public String        getNote()                     { return note; }
    public void          setNote(String n)             { this.note = n; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void          setCreatedAt(LocalDateTime t) { this.createdAt = t; }
}
