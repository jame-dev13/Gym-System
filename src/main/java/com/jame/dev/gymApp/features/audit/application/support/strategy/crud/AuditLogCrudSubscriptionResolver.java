package com.jame.dev.gymApp.features.audit.application.support.strategy.crud;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.model.SubscriptionBeforeUpdateModel;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogCrudPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.crud.AuditLogCrudEntityResolver;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditLogCrudSubscriptionResolver implements AuditLogCrudEntityResolver {
   @Override
   public AuditLogEntityType entity() {
      return AuditLogEntityType.SUBSCRIPTION;
   }

   @Override
   public AuditLogCrudPayload resolveUpdate(AuditExecutionContext ctx) {
      final var subscriptionRes = (SubscriptionResponse) ctx.getResultValue();
      final var subscriptionBeforeModel = (SubscriptionBeforeUpdateModel) ctx.getInput();
      return AuditLogCrudPayload.builder()
         .before(Map.of(
            "membership", subscriptionBeforeModel.membership(),
            "price", subscriptionBeforeModel.price(),
            "status", subscriptionBeforeModel.status()
         ))
         .after(Map.of(
            "membership", subscriptionRes.membership(),
            "price", subscriptionRes.price(),
            "status", subscriptionRes.status()
         ))
         .build();
   }

}
