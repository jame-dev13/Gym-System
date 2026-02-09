package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface Putable<E, DTO, ID> {
   E put(@NonNull ID id, @NonNull DTO dto);
}