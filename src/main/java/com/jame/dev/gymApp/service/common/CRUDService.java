package com.jame.dev.gymApp.service.common;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CRUDService<@NonNull E, @NonNull D> {
   List<E> getAll();
   List<E> getActives();
   Page<@NonNull E> getPageOfActives(@NonNull final Pageable pageable);
   Optional<E> getById(@NonNull Long id);
   E save(@NonNull final D dto);
   E update(@NonNull Long id, @NonNull final D dto);
   void softDeleteById(@NonNull Long id);
}
