package com.jsy.platform.quiz.api;

import java.util.List;

public record QuizDto(
        Long id,
        String question,
        List<String> options
) {
}
