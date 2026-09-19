package com.jsy.platform.progress.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "total_xp", nullable = false)
    private int totalXp = 0;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(columnDefinition = "TEXT")
    private String badges;

    @Column(name = "active_project_id")
    private Long activeProjectId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserProgress() {
    }

    public UserProgress(Long userId) {
        this.userId = userId;
        this.updatedAt = LocalDateTime.now();
    }

    public void addXp(int xp) {
        this.totalXp += xp;
        this.updatedAt = LocalDateTime.now();
    }

    public void deductXp(int xp) {
        this.totalXp = Math.max(0, this.totalXp - xp);
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementStreak() {
        this.currentStreak++;
        this.updatedAt = LocalDateTime.now();
    }

    public void resetStreak() {
        this.currentStreak = 0;
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public String getBadges() { return badges; }
    public void setBadges(String badges) { this.badges = badges; }

    public Long getActiveProjectId() { return activeProjectId; }
    public void setActiveProjectId(Long activeProjectId) { this.activeProjectId = activeProjectId; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
