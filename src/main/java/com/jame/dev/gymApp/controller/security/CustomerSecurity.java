package com.jame.dev.gymApp.controller.security;

import com.jame.dev.gymApp.oauth2.model.AuthenticatedUser;
import com.jame.dev.gymApp.service.common.OwnershipService;
import com.jame.dev.gymApp.service.in.CustomerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component("customerSecurity")
@RequiredArgsConstructor
public class CustomerSecurity implements OwnershipService {

   private final CustomerService customerService;

   @Override
   public boolean isOwner(long id, @NonNull Authentication authentication) {
      final String authName = getAuthName(authentication);
      if (authName == null || authName.isBlank()) {
         return false;
      }
      return customerService.exitsByIdAndCustomerEmail(id, authName);
   }

   private String getAuthName(@NonNull final Authentication authentication) {
      if (authentication.getPrincipal() instanceof AuthenticatedUser user)
         return user.email();
      return authentication.getName();
   }
}
