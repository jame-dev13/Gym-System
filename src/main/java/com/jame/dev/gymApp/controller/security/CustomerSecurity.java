package com.jame.dev.gymApp.controller.security;

import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import com.jame.dev.gymApp.service.common.CustomerOwnershipService;
import com.jame.dev.gymApp.service.in.CustomerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component("customerSecurity")
@RequiredArgsConstructor
public class CustomerSecurity implements CustomerOwnershipService {

   private final CustomerService customerService;

   @Override
   public boolean isOwner(final long id, @NonNull final Authentication authentication) {
      final String authName = getAuthName(authentication);
      if (authName == null || authName.isBlank()) {
         return false;
      }
      return customerService.exitsByIdAndCustomerEmail(id, authName);
   }

   @Override
   public boolean isOwner(final String email, @NonNull final Authentication authentication) {
      final String authName = getAuthName(authentication);
      if(authName == null || authName.isBlank())
         return false;
      return email.equals(authName);
   }

   @Override
   public boolean isOwner(CustomerDtoInput input, @org.jspecify.annotations.NonNull Authentication authentication) {
      final String authName = getAuthName(authentication);
      if(authName == null || authName.isBlank())
         return false;
      return input.email().equals(authName);
   }

   private String getAuthName(@NonNull final Authentication authentication) {
      if (authentication.getPrincipal() instanceof CustomOAuth2User user)
         return user.getUser().email();
      return authentication.getName();
   }
}
