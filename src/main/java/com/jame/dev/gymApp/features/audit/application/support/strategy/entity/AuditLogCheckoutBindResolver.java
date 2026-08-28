package com.jame.dev.gymApp.features.audit.application.support.strategy.entity;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.entity.AuditActionBindResolver;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionActor;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuditLogCheckoutBindResolver implements AuditActionBindResolver {
   private final SubscriptionQueryRepository subscriptionQueryRepository;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.CHECKOUT;
   }

   @Override
   public void resolveIdentity(AuditExecutionContext context) {
      final Long subscriptionId = Objects.requireNonNull(context.getEntityId(), "Entity id required, but not found.");
      final SubscriptionActor subscriptionActor = subscriptionQueryRepository.findSubscriptionActorById(subscriptionId)
         .orElseThrow(() -> new NotFoundException("Subscription actor not found for: " + subscriptionId));

      context.setAuditLogActor(
         AuditLogActor.builder()
            .userId(subscriptionActor.ownerId())
            .username(subscriptionActor.owerName())
            .build()
      );
   }
}
