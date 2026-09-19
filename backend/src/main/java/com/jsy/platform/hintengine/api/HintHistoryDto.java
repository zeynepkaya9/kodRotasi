package com.jsy.platform.hintengine.api;

import java.util.List;

public record HintHistoryDto(
        List<HintResponseDto> revealedHints,
        int remainingHints
) {
}
