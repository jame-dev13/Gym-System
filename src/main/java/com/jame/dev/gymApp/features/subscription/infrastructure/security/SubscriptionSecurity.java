package com.jame.dev.gymApp.features.subscription.infrastructure.security;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionOwnershipService;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionService;
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
   public boolean isOwner(SubscriptionRequest input, @NonNull Authentication authentication) {
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
