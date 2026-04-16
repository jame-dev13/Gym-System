package com.jame.dev.gymApp.validators;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.NoActiveException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerValidator {
   private final UserRepository userRepository;
   private final CustomerRepository customerRepository;

   public UserEntity validateUserBeforeCreation(final CustomerDtoInput dto) {
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
