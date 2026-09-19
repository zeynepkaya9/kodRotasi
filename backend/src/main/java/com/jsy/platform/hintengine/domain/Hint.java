package com.jsy.platform.hintengine.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "hint",
        uniqueConstraints = @UniqueConstraint(columnNames = {"step_id", "hint_level"}))
public class Hint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_id", nullable = false)
    private Long stepId;

    @Enumerated(EnumType.STRING)
    @Column(name = "hint_level", nullable = false)
    private HintLevel hintLevel;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public Hint() {
    }

    public Hint(Long stepId, HintLevel hintLevel, String content) {
        this.stepId = stepId;
        this.hintLevel = hintLevel;
        this.content = content;
    }

    public Long getId() { return id; }

    public Long getStepId() { return stepId; }
    public void setStepId(Long stepId) { this.stepId = stepId; }

    public HintLevel getHintLevel() { return hintLevel; }
    public void setHintLevel(HintLevel hintLevel) { this.hintLevel = hintLevel; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
