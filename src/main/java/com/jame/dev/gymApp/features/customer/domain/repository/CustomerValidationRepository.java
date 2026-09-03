package com.jame.dev.gymApp.features.customer.domain.repository;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;

public interface CustomerValidationRepository {

   boolean existsByUser(final UserEntity userEntity);

   boolean existByIdAndActiveFalse(final long id);

   boolean existsByIdAndUserEmail(final long id, final String email);

   boolean existsByUserEmail(final String userEmail);
}
