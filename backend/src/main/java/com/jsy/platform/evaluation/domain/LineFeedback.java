package com.jsy.platform.evaluation.domain;

public record LineFeedback(
        String ruleId,
        String status,
        String message,
        String why,
        String alternative,
        String realWorld
) {
}
