package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.aspects.annotations.DoNotFilter;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
import lombok.NonNull;

import java.util.Optional;

public interface UserRepository extends CustomJpaRepository<UserEntity, Long> {
   @DoNotFilter
   Optional<UserEntity> findByEmail(@NonNull final String email);

   @DoNotFilter
   boolean existsByEmail(@NonNull final String email);
}
