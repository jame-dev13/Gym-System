package com.jame.dev.gymApp.service.common;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CRUDServiceServicePut<@NonNull E, @NonNull D, @NonNull ID> extends
        BaseCrudService<E, D, ID>,
        Updatable<E, D, ID> {
   @Override
   Page<@NonNull E> getPage(final @NonNull Pageable pageable);

   @Override
   E save(@NonNull final D d);

   @Override
   Optional<E> getById(@NonNull final ID id);

   @Override
   void softDelete(@NonNull final ID id);

   @Override
   E update(@NonNull ID id, @NonNull D d);
}
