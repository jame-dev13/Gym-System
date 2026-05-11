package com.jame.dev.gymApp.application.contract;

import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.Valid;

public interface Putable<E, DTO> {
   E put(@Minimum long id, @NotNullObject @Valid DTO dto);
}