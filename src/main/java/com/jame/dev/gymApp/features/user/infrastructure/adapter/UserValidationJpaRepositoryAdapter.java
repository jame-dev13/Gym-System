package com.jame.dev.gymApp.features.user.infrastructure.adapter;

import com.jame.dev.gymApp.features.user.domain.repository.UserValidationRepository;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserValidationJpaRepositoryAdapter implements UserValidationRepository {
   private final UserRepository userRepository;

   @Override
   public boolean existsByEmail(String email) {
      return userRepository.existsByEmail(email);
   }

   @Override
   public boolean existsAndIsDeactivatedByEmail(String email) {
      return userRepository.existsAndIsDeactivatedByEmail(email);
   }

   @Override
   public boolean existsByIdAndEmail(long id, String email) {
      return userRepository.existsByIdAndEmail(id, email);
   }

   @Override
   public boolean existsByIdAndNotActive(long id) {
      return userRepository.existsByIdAndActiveFalse(id);
   }
}
