package com.jame.dev.gymApp.service.common;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CRUDServiceServicePatch<@NonNull E, @NonNull D, ID> extends
        BaseCrudService<E, D, ID>,
        Patchable<E, ID> {
   @Override
   Page<@NonNull E> getPage(final @NonNull Pageable pageable);

   @Override
   E save(@NonNull final D d);

   @Override
   Optional<E> getById(@NonNull final ID id);

   @Override
   void softDelete(@NonNull final ID id);

   @Override
   E patch(ID Id);
}
