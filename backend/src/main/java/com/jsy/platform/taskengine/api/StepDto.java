package com.jsy.platform.taskengine.api;

import com.jsy.platform.taskengine.domain.StepStatus;

public record StepDto(
        Long id,
        int orderIndex,
        String instruction,
        String learningNotes,
        String starterCode,
        StepStatus status,
        int attempts,
        int hintsUsed,
        String savedCode,
        java.time.Instant savedAt
) {
}
