package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.aspects.annotations.Minimum;
import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import jakarta.validation.Valid;

public interface Putable<E, DTO> {
   E put(@Minimum long id, @NotNullObject @Valid DTO dto);
}