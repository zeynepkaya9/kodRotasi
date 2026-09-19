package com.jsy.platform.quiz.api;

public record QuizResultDto(
        boolean correct,
        String explanation
) {
}
