package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface Updatable <E, DTO, ID>{
   E update(@NonNull ID id, @NonNull DTO dto);
}
