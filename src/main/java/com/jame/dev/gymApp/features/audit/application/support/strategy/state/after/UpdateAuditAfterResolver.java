package com.jame.dev.gymApp.features.audit.application.support.strategy.state.after;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditAfterResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateAuditAfterResolver implements AuditAfterResolver {
   private final AuditLogExpressionEvaluator evaluator;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.UPDATE;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      context.setResultValue(evaluator.evaluateAsObject(context.getAnnotation().result(), context.getResult()));
   }
}
