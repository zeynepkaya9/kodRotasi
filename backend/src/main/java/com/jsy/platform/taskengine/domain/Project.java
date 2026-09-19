package com.jsy.platform.taskengine.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jsy.platform.learningpath.domain.Level;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private ProjectCode code;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "min_level", nullable = false)
    private Level minLevel;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();

    public Project() {
    }

    public Project(ProjectCode code, String title, String description, Level minLevel) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.minLevel = minLevel;
    }

    public Long getId() { return id; }

    public ProjectCode getCode() { return code; }
    public void setCode(ProjectCode code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Level getMinLevel() { return minLevel; }
    public void setMinLevel(Level minLevel) { this.minLevel = minLevel; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }
}
