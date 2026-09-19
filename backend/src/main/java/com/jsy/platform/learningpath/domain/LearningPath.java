package com.jsy.platform.learningpath.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_path")
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommended_arch")
    private Architecture recommendedArchitecture;

    @Enumerated(EnumType.STRING)
    @Column(name = "chosen_arch")
    private Architecture chosenArchitecture;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public LearningPath() {
    }

    public LearningPath(Long userId, Level level, Architecture recommendedArchitecture) {
        this.userId = userId;
        this.level = level;
        this.recommendedArchitecture = recommendedArchitecture;
        this.chosenArchitecture = recommendedArchitecture;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

    public Architecture getRecommendedArchitecture() { return recommendedArchitecture; }
    public void setRecommendedArchitecture(Architecture arch) { this.recommendedArchitecture = arch; }

    public Architecture getChosenArchitecture() { return chosenArchitecture; }
    public void setChosenArchitecture(Architecture arch) { this.chosenArchitecture = arch; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
