package com.jsy.platform.learningpath.api;

import java.util.List;

public record AssessmentSubmitRequest(
        List<Integer> answers
) {
}
