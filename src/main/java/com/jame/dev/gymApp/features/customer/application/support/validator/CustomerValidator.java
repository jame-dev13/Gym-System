package com.jame.dev.gymApp.features.customer.application.support.validator;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.customer.api.request.CustomerCreateRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerValidationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.persistence.CustomerRepository;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerValidator {
   private final UserQueryRepository userQueryRepository;
   private final UserValidationRepository userValidationRepository;
   private final CustomerValidationRepository customerValidationRepository;
   private final CustomerRepository customerRepository;

   public UserEntity validateUserBeforeCreation(final CustomerRequest dto) {
      final UserEntity user = userQueryRepository.findByEmail(dto.email())
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));

      if (!user.isActive()) {
         throw new NoActiveException("This user's account is deactivated.");
      }

      if (customerRepository.existsByUser(user)) {
         throw new AlreadyExistsException("Customer Already exists.");
      }

      if (customerRepository.existsByIdAndActiveFalse(user.getId())) {
         throw new NoActiveException("Account is deactivated.");
      }

      return user;
   }

   public UserEntity validateUserBeforeCreation(final CustomerCreateRequest request) {
      final long userId = request.userId();
      if (userValidationRepository.existsByIdAndNotActive(userId)) {
         throw new NoActiveException("User exists but is unactive.");
      }

      final var user = userQueryRepository.findById(userId)
         .orElseThrow(UserEntityNotFoundException::new);

      if (customerValidationRepository.existsByUser(user)) {
         throw new AlreadyExistsException("Customer Already exists.");
      }

      if (customerValidationRepository.existByIdAndActiveFalse(userId)) {
         throw new NoActiveException("Account is deactivated.");
      }

      return user;
   }
}
