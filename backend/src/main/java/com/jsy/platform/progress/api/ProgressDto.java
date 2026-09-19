package com.jsy.platform.progress.api;

public record ProgressDto(
        int totalXp,
        int currentStreak,
        String badges,
        int completedSteps,
        int totalSteps
) {
}
