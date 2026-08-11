package com.jame.dev.gymApp.features.audit.application.support.strategy.state.before;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignInAuditBeforeResolver implements AuditBeforeResolver {
   private final AuditLogExpressionEvaluator evaluator;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.SIGN_IN;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      context.setInput(
         evaluator.evaluateAsObject(
            context.getAnnotation().input(),
            context.getParamNames(),
            context.getArgs())
      );
   }
}
