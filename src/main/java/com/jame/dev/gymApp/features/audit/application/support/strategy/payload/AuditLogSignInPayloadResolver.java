package com.jame.dev.gymApp.features.audit.application.support.strategy.payload;

import com.jame.dev.gymApp.features.audit.application.model.AuditAuthenticationResultValue;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAuthPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.payload.AuditLogPayloadResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.utils.AuditAuthInfoMapExtractor;
import org.springframework.stereotype.Component;

@Component
public class AuditLogSignInPayloadResolver implements AuditLogPayloadResolver {

   @Override
   public AuditLogAction action() {
      return AuditLogAction.SIGN_IN;
   }

   @Override
   public AuditPayload resolve(AuditExecutionContext ctx) {
      final var res = (AuditAuthenticationResultValue) ctx.getResultValue();
      return AuditLogAuthPayload.builder()
         .info(AuditAuthInfoMapExtractor.extractInfoMapFrom(ctx.getTh(), res))
         .build();
   }
}
