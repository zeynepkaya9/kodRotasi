package com.jsy.platform.taskengine.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DraftRequest(@NotNull @Size(max = 100000) String code) {}
