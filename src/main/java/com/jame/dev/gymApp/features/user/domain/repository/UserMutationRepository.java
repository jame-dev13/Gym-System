package com.jame.dev.gymApp.features.user.domain.repository;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;

public interface UserMutationRepository {
   UserEntity save(final UserEntity userEntity);
   void deleteById(final long id);
   void hardDeleteById(final long id);
}
