package com.jsy.platform.hintengine.api;

import com.jsy.platform.hintengine.domain.HintLevel;

public record HintResponseDto(
        HintLevel hintLevel,
        String content,
        int xpPenalty,
        int remainingHints
) {
}
