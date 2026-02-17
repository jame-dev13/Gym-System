package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.aspects.annotations.DoNotFilter;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;

import java.util.Optional;

public interface CustomerRepository extends CustomJpaRepository<CustomerEntity, Long> {
   Optional<CustomerEntity> findByUser_Email(final String email);

   @DoNotFilter
   Optional<CustomerEntity> findByUser(final UserEntity user);

   boolean existsByIdAndUser_EmailAndActiveTrue(long id, String email);
}
