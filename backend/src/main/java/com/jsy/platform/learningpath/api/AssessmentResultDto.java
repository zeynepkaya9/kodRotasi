package com.jsy.platform.learningpath.api;

import com.jsy.platform.learningpath.domain.Architecture;
import com.jsy.platform.learningpath.domain.Level;

public record AssessmentResultDto(
        Level level,
        double score,
        Architecture recommendedArchitecture,
        String architectureReasoning
) {
}
