package com.jsy.platform.learningpath.api;

import java.util.List;

public record AssessmentQuestionDto(
        int id,
        String question,
        List<String> options,
        int correctIndex,
        int weight,
        String topic
) {
}
