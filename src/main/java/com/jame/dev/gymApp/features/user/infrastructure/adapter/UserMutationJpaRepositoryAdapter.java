package com.jame.dev.gymApp.features.user.infrastructure.adapter;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserMutationJpaRepositoryAdapter implements UserMutationRepository {

   private final UserRepository userRepository;

   @Override
   public UserEntity save(UserEntity userEntity) {
      return userRepository.saveAndFlush(userEntity);
   }

   @Override
   public void deleteById(long id) {
      userRepository.deleteById(id);
   }

   @Override
   public void hardDeleteById(long id) {
      userRepository.hardDelete(id);
   }
}
