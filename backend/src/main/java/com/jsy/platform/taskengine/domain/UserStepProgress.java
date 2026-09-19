package com.jsy.platform.taskengine.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_step_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "step_id"}))
public class UserStepProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "step_id", nullable = false)
    private Long stepId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StepStatus status = StepStatus.LOCKED;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(name = "hints_used", nullable = false)
    private int hintsUsed = 0;

    @Column(name = "last_submission", columnDefinition = "TEXT")
    private String lastSubmission;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "draft_code", columnDefinition = "TEXT")
    private String draftCode;

    private java.time.Instant draftUpdatedAt;

    public void saveDraft(String code) {
        this.draftCode = code;
        this.draftUpdatedAt = java.time.Instant.now();
    }

    public String getDraftCode() { return draftCode; }
    public java.time.Instant getDraftUpdatedAt() { return draftUpdatedAt; }

    public UserStepProgress() {
    }

    public UserStepProgress(Long userId, Long stepId, StepStatus status) {
        this.userId = userId;
        this.stepId = stepId;
        this.status = status;
    }

    public void markCompleted() {
        this.status = StepStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public void incrementHintsUsed() {
        this.hintsUsed++;
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public Long getStepId() { return stepId; }

    public StepStatus getStatus() { return status; }
    public void setStatus(StepStatus status) { this.status = status; }

    public int getAttempts() { return attempts; }

    public int getHintsUsed() { return hintsUsed; }

    public String getLastSubmission() { return lastSubmission; }
    public void setLastSubmission(String lastSubmission) { this.lastSubmission = lastSubmission; }

    public LocalDateTime getCompletedAt() { return completedAt; }
}
