package com.jame.dev.gymApp.features.customer.domain.repository;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;

public interface CustomerValidationRepository {

   boolean existsByUser(final UserEntity userEntity);

   boolean existByUserIdAndActiveFalse(final long userId);

   boolean existsByIdAndUserEmail(final long id, final String email);
}
