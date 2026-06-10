package com.jame.dev.gymApp.features.customer.infrastructure.adapter;

import com.jame.dev.gymApp.features.customer.domain.repository.CustomerValidationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.persistence.CustomerRepository;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerValidationRepositoryJpaAdapter implements CustomerValidationRepository {
   private final CustomerRepository customerRepository;

   @Override
   public boolean existsByUser(UserEntity userEntity) {
      return customerRepository.existsByUser(userEntity);
   }

   @Override
   public boolean existByUserIdAndActiveFalse(long userId) {
      return customerRepository.existsByUserIdAndActiveFalse(userId);
   }

   @Override
   public boolean existsByIdAndUserEmail(long id, String email) {
      return customerRepository.existsByIdAndUser_Email(id, email);
   }
}
