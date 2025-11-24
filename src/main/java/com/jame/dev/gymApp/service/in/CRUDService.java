package com.jame.dev.gymApp.service.in;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface CRUDService<@NonNull T, @NonNull D> {
   List<T> getAll();
   Optional<T> getById(@NonNull Long id);
   T save(@NonNull final D dto);
   T update(@NonNull final D dto);
   void softDeleteById(@NonNull Long id);
}
