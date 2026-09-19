package com.jsy.platform.taskengine.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference
    private Project project;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String objective;

    @Column(name = "concept_codes")
    private String conceptCodes;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference
    private List<Step> steps = new ArrayList<>();

    public Task() {
    }

    public Task(int orderIndex, String title, String objective, String conceptCodes) {
        this.orderIndex = orderIndex;
        this.title = title;
        this.objective = objective;
        this.conceptCodes = conceptCodes;
    }

    public Long getId() { return id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }

    public String getConceptCodes() { return conceptCodes; }
    public void setConceptCodes(String conceptCodes) { this.conceptCodes = conceptCodes; }

    public List<Step> getSteps() { return steps; }
    public void setSteps(List<Step> steps) { this.steps = steps; }

    public void addStep(Step step) {
        steps.add(step);
        step.setTask(this);
    }
}
