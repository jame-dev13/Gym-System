package com.jame.dev.gymApp.features.subscription.infrastructure.security;

import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionOwnershipService;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component("subscriptionSecurity")
@RequiredArgsConstructor
public class SubscriptionSecurity implements SubscriptionOwnershipService {

   private final SubscriptionValidationRepository subscriptionValidationRepository;
   private final IdentityExtractorService extractorService;

   @Override
   public boolean isOwner(long id, @NonNull Authentication authentication) {
      final String authName = extractorService.extract(authentication);
      if (authName == null || authName.isBlank())
         return false;
      return subscriptionValidationRepository.existsByIdAndCustomerEmail(id, authName);
   }

   @Override
   public boolean isOwner(String email, @NonNull Authentication authentication) {
      final String authName = extractorService.extract(authentication);
      if (authName == null || authName.isBlank())
         return false;
      return email.equals(authName);
   }

   @Override
   public boolean isOwner(SubscriptionRequest input, @NonNull Authentication authentication) {
      final String authName = extractorService.extract(authentication);
      if (authName == null || authName.isBlank())
         return false;
      return input.customerEmail().equals(authName);
   }

}
