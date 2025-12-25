package com.jame.dev.gymApp.controller.components;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.service.common.OwnershipService;
import com.jame.dev.gymApp.service.in.CustomerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("customerSecurity")
@RequiredArgsConstructor
public class CustomerSecurity implements OwnershipService<Long> {

   private final CustomerService customerService;

   public boolean isOwner(@NonNull final Long id, @NonNull final Authentication authentication) {
      return customerService.getById(id)
              .map(CustomerEntity::getUser)
              .map(UserEntity::getEmail)
              .map(email -> email.equals(authentication.getName()))
              .orElse(false);
   }
}
