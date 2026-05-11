package com.jame.dev.gymApp.features.customer.application.support.validator;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerValidator {
   private final UserRepository userRepository;
   private final CustomerRepository customerRepository;

   public UserEntity validateUserBeforeCreation(final CustomerRequest dto) {
      final UserEntity user = userRepository.findByEmail(dto.email())
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));

      if (!user.isActive()) {
         throw new NoActiveException("This user's account is deactivated.");
      }

      customerRepository.findDeactivatedByUserId(user.getId())
         .ifPresent(customer -> {
            if (!customer.isActive()) {
               throw new NoActiveException("Account is deactivated.");
            }
            throw new AlreadyExistsException("Customer Already exists.");
         });
      return user;
   }
}
