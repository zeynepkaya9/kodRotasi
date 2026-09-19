package com.jsy.platform.taskengine.api;

import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.taskengine.domain.ProjectCode;

public record ProjectDto(
        Long id,
        ProjectCode code,
        String title,
        String description,
        Level minLevel,
        int taskCount
) {
}
