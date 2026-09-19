package com.jsy.platform.evaluation.api;

import com.jsy.platform.evaluation.domain.LineFeedback;

import java.util.List;

public record EvaluationResponse(
        boolean passed,
        List<LineFeedback> feedback,
        int xpEarned,
        List<String> conceptsToReview
) {
}
