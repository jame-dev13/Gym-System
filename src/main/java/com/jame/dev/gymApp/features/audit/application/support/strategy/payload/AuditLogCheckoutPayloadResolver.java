package com.jame.dev.gymApp.features.audit.application.support.strategy.payload;

import com.jame.dev.gymApp.features.audit.application.model.AfterCompletedCheckoutModel;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.model.BeforeCompletedCheckoutModel;
import com.jame.dev.gymApp.features.audit.domain.exception.AuditResolverException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogCrudPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.payload.AuditLogPayloadResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditLogCheckoutPayloadResolver implements AuditLogPayloadResolver {
   @Override
   public AuditLogAction action() {
      return AuditLogAction.CHECKOUT;
   }

   @Override
   public AuditPayload resolve(AuditExecutionContext ctx) {
      if (!(ctx.getInput() instanceof BeforeCompletedCheckoutModel before) ||
          !(ctx.getResultValue() instanceof AfterCompletedCheckoutModel after)) {
         throw new AuditResolverException("Cannot resolve AuditPayload for: " + getClass().getSimpleName());
      }

      return AuditLogCrudPayload.builder()
         .before(Map.of(
            "paymentStatus", before.paymentStatus(),
            "owner", before.customerEmail(),
            "amount", before.amount()
         ))
         .after(Map.of(
            "paymentStatus", after.paymentStatus(),
            "owner", after.customerEmail(),
            "amount", after.amount(),
            "reason", "Subscription payment for: " + after.membership() + " membership.",
            "subscriptionStatus", after.subscriptionStatus()
         ))
         .build();
   }
}
