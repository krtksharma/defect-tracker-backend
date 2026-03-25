package com.cognizant.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "Defect")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Defect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "defectdetails", length = 500)
    private String defectdetails;

    @Column(name = "stepstoreproduce", length = 1000)
    private String stepstoreproduce;

    @Column(name = "priority", length = 10)
    @Pattern(regexp = "P[123]", message = "Priority must be P1, P2, or P3")
    private String priority;

    @Column(name = "detectedon")
    @NotNull
    private LocalDate detectedon;

    @Column(name = "expectedresolution")
    @FutureOrPresent(message = "ExpectedResolution must be today or a future date")
    private LocalDate expectedresolution;

    @Column(name = "reportedbytesterid", length = 50)
    private String reportedbytesterid;

    @Column(name = "assignedtodeveloperid", length = 50)
    private String assignedtodeveloperid;

    @Column(name = "severity", length = 20)
    @Pattern(regexp = "Blocking|Critical|Major|Minor|Low",
             message = "Severity must be one of: Blocking, Critical, Major, Minor, Low")
    private String severity;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "projectcode")
    private int projectcode;

    // ── NEW: Expected vs Actual behavior ─────────────────────────────
    @Column(name = "expectedbehavior", length = 1000)
    private String expectedbehavior;

    @Column(name = "actualbehavior", length = 1000)
    private String actualbehavior;
    // ─────────────────────────────────────────────────────────────────

    @OneToMany(mappedBy = "defect", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Resolution> resolutions = new ArrayList<>();

    // ── ADD: Comments ─────────────────────────────────────────────────
    @OneToMany(mappedBy = "defect", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    // ── ADD: Attachments ──────────────────────────────────────────────
    @OneToMany(mappedBy = "defect", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Attachment> attachments = new ArrayList<>();

    // Custom setter — truncates stepstoreproduce to 10 words
    public void setStepstoreproduce(String stepstoreproduce) {
        if (stepstoreproduce == null) { this.stepstoreproduce = null; return; }
        String[] words = stepstoreproduce.split("\\s+");
        int max = Math.min(words.length, 10);
        this.stepstoreproduce = String.join(" ", Arrays.copyOfRange(words, 0, max));
    }
}
