package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface CRUDStaticService<E> {
   List<E> getAll();
   E save(@NonNull final E entity);
   Optional<E> findById(@NonNull final Integer id);
   void deleteById(@NonNull final Integer id);
}
