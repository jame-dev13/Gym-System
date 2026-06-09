package com.jame.dev.gymApp.features.user.domain.repository;


import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface UserQueryRepository {
   Page<UserEntity> findAll(final Pageable pageable, final Specification<UserEntity> specification);
   Page<UserMinimalInfoResponse> findAllDeactivated(final Pageable pageable, final String search);
   Optional<UserEntity> findById(final long id);
   Optional<UserEntity> findByEmail(final String email);
   Optional<UserEntity> findDeactivatedById(final long id);
}
