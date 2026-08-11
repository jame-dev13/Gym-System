package com.jame.dev.gymApp.features.audit.application.support.strategy.state.before;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterAuditBeforeResolver implements AuditBeforeResolver {
   private final AuditLogExpressionEvaluator evaluator;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.REGISTER;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      context.setInput(
         evaluator.evaluateAsObject(context.getAnnotation().input(), context.getParamNames(), context.getArgs())
      );
   }
}
