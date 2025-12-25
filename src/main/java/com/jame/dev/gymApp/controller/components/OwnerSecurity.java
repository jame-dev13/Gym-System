package com.jame.dev.gymApp.controller.components;

import com.jame.dev.gymApp.service.common.OwnershipService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("ownerSecurity")
@RequiredArgsConstructor
public class OwnerSecurity implements OwnershipService<Long> {

   private final CustomerSecurity customerSecurity;
   private final SubscriptionSecurity subscriptionSecurity;

   @Override
   public boolean isOwner(Long id, @NonNull Authentication authentication) {
      return customerSecurity.isOwner(id, authentication) ||
              subscriptionSecurity.isOwner(id,  authentication);
   }
}
