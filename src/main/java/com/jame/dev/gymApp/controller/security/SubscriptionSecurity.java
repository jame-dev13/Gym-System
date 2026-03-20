package com.jame.dev.gymApp.controller.security;

import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import com.jame.dev.gymApp.service.common.SubscriptionOwnershipService;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component("subscriptionSecurity")
@RequiredArgsConstructor
public class SubscriptionSecurity implements SubscriptionOwnershipService {

   private final SubscriptionService subscriptionService;

   @Override
   public boolean isOwner(long id, @NonNull Authentication authentication) {
     final String authName = getAuthName(authentication);
     if(authName == null || authName.isBlank())
        return false;
     return subscriptionService.exitsByIdAndCustomerEmail(id, authName);
   }

   @Override
   public boolean isOwner(String email, @NonNull Authentication authentication) {
      final String authName = getAuthName(authentication);
      if(authName == null || authName.isBlank())
         return false;
      return email.equals(authName);
   }

   @Override
   public boolean isOwner(SubscriptionDtoInput input, @NonNull Authentication authentication) {
      final String authName = getAuthName(authentication);
      if(authName == null || authName.isBlank())
         return false;
      return input.customerEmail().equals(authName);
   }

   private String getAuthName(@NonNull final Authentication authentication) {
      if (authentication.getPrincipal() instanceof CustomOAuth2User user)
         return user.getUser().email();
      return authentication.getName();
   }
}
