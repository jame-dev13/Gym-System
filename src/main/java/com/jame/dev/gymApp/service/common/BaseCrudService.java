package com.jame.dev.gymApp.service.common;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface  BaseCrudService<E, DTO_IN, ID> {
   Page<@NonNull E> getPage(@NonNull final Pageable pageable);

   E save(@NonNull final DTO_IN dtoIn);

   Optional<E> getById(@NonNull final ID id);

   E update(final ID id, @NonNull final DTO_IN dtoIn);

   void softDelete(@NonNull final ID id);
}
