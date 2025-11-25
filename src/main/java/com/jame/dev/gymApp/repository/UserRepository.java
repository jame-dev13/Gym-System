package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
import lombok.NonNull;

import java.util.Optional;

public interface UserRepository extends CustomJpaRepository<UserEntity, Long> {
   Optional<UserEntity> findByEmail(@NonNull final  String email);
}
