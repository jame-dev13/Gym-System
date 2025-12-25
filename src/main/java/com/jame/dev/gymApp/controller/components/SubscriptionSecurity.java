package com.jame.dev.gymApp.controller.components;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.service.common.OwnershipService;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("subscriptionSecurity")
@RequiredArgsConstructor
public class SubscriptionSecurity implements OwnershipService<Long> {

   private final SubscriptionService subscriptionService;
   @Override
   public boolean isOwner(Long id, @NonNull Authentication authentication) {
      return subscriptionService.getById(id)
              .map(SubscriptionEntity::getCustomer)
              .map(CustomerEntity::getUser)
              .map(UserEntity::getEmail)
              .map(email -> email.equals(authentication.getName()))
              .orElse(false);
   }
}
