package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface CRUDService<@NonNull E, @NonNull D> {
   List<E> getAll();
   Optional<E> getById(@NonNull Long id);
   E save(@NonNull final D dto);
   E update(@NonNull final D dto);
   void softDeleteById(@NonNull Long id);
}
