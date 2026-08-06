package com.jame.dev.gymApp.features.audit.application.support.strategy;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.parser.LongParser;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecoverAuditBeforeResolver implements AuditBeforeResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final LongParser longParser;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.RECOVER;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final String idStr = evaluator.evaluate(context.getAnnotation().entityId(), context.getParamNames(), context.getArgs());
      final Long id = longParser.parseString(idStr);
      final Object input = evaluator.evaluateAsObject(context.getAnnotation().input(), context.getParamNames(), context.getArgs());
      context.setEntityId(id);
      context.setInput(input);
   }
}
