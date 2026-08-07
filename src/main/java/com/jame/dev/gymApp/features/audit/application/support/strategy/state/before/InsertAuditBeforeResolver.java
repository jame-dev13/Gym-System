package com.jame.dev.gymApp.features.audit.application.support.strategy.state.before;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsertAuditBeforeResolver implements AuditBeforeResolver {
   private final AuditLogExpressionEvaluator evaluator;


   @Override
   public AuditLogAction action() {
      return AuditLogAction.INSERT;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final Object input = evaluator.evaluateAsObject(context.getAnnotation().input(), context.getParamNames(), context.getArgs());
      context.setInput(input);
   }
}
