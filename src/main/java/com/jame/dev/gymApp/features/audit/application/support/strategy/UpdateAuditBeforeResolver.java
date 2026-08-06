package com.jame.dev.gymApp.features.audit.application.support.strategy;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.support.resolver.AuditIdQueryBeforeResolver;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.parser.LongParser;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateAuditBeforeResolver implements AuditBeforeResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final AuditIdQueryBeforeResolver auditIdQueryBeforeResolver;
   private final LongParser longParser;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.UPDATE;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final var annotation = context.getAnnotation();
      final String entityIdStr = evaluator.evaluate(annotation.entityId(), context.getParamNames(), context.getArgs());
      final Long entityId = longParser.parseString(entityIdStr);
      final Object input = auditIdQueryBeforeResolver.getStateBeforeOfById(annotation.entityType(), entityId);
      context.setInput(input);
      context.setEntityId(entityId);
   }
}
