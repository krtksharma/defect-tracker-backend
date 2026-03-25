package com.cognizant.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * In-app notifications — stored in DB, fetched by frontend.
 * type: BUG_ASSIGNED | COMMENT_ADDED | STATUS_CHANGED | BUG_RESOLVED
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Who should receive this notification
    @Column(name = "recipient", nullable = false, length = 50)
    private String recipient;

    @Column(name = "type", nullable = false, length = 40)
    private String type;         // BUG_ASSIGNED | COMMENT_ADDED | STATUS_CHANGED

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "defect_id")
    private Integer defectId;

    @Column(name = "defect_title", length = 200)
    private String defectTitle;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    // Factory helpers
    public static Notification bugAssigned(String developer, Integer defectId, String defectTitle, String assignedBy) {
        Notification n = new Notification();
        n.setRecipient(developer);
        n.setType("BUG_ASSIGNED");
        n.setDefectId(defectId);
        n.setDefectTitle(defectTitle);
        n.setMessage(assignedBy + " assigned bug #" + defectId + " \"" + defectTitle + "\" to you");
        return n;
    }

    public static Notification commentAdded(String recipient, Integer defectId, String defectTitle, String commenter) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setType("COMMENT_ADDED");
        n.setDefectId(defectId);
        n.setDefectTitle(defectTitle);
        n.setMessage(commenter + " commented on bug #" + defectId + " \"" + defectTitle + "\"");
        return n;
    }

    public static Notification statusChanged(String recipient, Integer defectId, String defectTitle, String changedBy, String newStatus) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setType("STATUS_CHANGED");
        n.setDefectId(defectId);
        n.setDefectTitle(defectTitle);
        n.setMessage(changedBy + " changed status of bug #" + defectId + " to \"" + newStatus + "\"");
        return n;
    }

    // Getters & Setters
    public Integer       getId()                         { return id; }
    public void          setId(Integer id)               { this.id = id; }
    public String        getRecipient()                  { return recipient; }
    public void          setRecipient(String r)          { this.recipient = r; }
    public String        getType()                       { return type; }
    public void          setType(String t)               { this.type = t; }
    public String        getMessage()                    { return message; }
    public void          setMessage(String m)            { this.message = m; }
    public Integer       getDefectId()                   { return defectId; }
    public void          setDefectId(Integer d)          { this.defectId = d; }
    public String        getDefectTitle()                { return defectTitle; }
    public void          setDefectTitle(String t)        { this.defectTitle = t; }
    public boolean       isRead()                        { return isRead; }
    public void          setRead(boolean r)              { this.isRead = r; }
    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void          setCreatedAt(LocalDateTime t)   { this.createdAt = t; }
}
