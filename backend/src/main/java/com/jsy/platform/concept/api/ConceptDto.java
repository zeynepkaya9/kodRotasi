package com.jsy.platform.concept.api;

public record ConceptDto(
        Long id,
        String code,
        String title,
        String realLifeAnalogy,
        String whyExplanation,
        String wrongExample,
        String wrongExampleExplanation,
        String rightExample,
        String rightExampleExplanation
) {
}
