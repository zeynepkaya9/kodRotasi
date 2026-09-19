package com.jsy.platform.taskengine.api;

import java.util.List;

public record TaskDto(
        Long id,
        int orderIndex,
        String title,
        String objective,
        String conceptCodes,
        List<StepDto> steps
) {
}
