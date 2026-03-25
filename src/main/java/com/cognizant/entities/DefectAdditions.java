package com.cognizant.entities;

// ADD these two collections to your existing Defect entity.
// Paste inside the Defect class body alongside existing fields.

/*

    // ── ADD: Comments linked to this defect ───────────────────────────
    @OneToMany(mappedBy = "defect", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    // ── ADD: Attachments linked to this defect ────────────────────────
    @OneToMany(mappedBy = "defect", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Attachment> attachments = new ArrayList<>();

    // ── ADD getters/setters ───────────────────────────────────────────
    public List<Comment>    getComments()                          { return comments; }
    public void             setComments(List<Comment> c)           { this.comments = c; }

    public List<Attachment> getAttachments()                       { return attachments; }
    public void             setAttachments(List<Attachment> a)     { this.attachments = a; }

*/

// NOTE: This file is for reference only.
// Copy the above code block into your existing Defect.java entity.
// Your existing Defect.java already has: @OneToMany for Resolution.
// Add the same pattern for Comment and Attachment.

public class DefectAdditions {
    // placeholder — see comments above
}
