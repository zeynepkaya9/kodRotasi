package com.jsy.platform.learningpath.api;

import com.jsy.platform.learningpath.domain.Architecture;
import com.jsy.platform.learningpath.domain.Level;

public record LearningPathDto(
        Level level,
        Architecture recommendedArchitecture,
        Architecture chosenArchitecture
) {
}
