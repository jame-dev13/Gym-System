package com.jame.dev.gymApp.features.audit.application.support.strategy.state.before;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.support.resolver.AuditCurrentEntityIdBeforeResolver;
import com.jame.dev.gymApp.features.audit.application.support.resolver.AuditIdQueryBeforeResolver;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.parser.LongParser;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateAuditBeforeResolver implements AuditBeforeResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final AuditCurrentEntityIdBeforeResolver entityIdBeforeResolver;
   private final AuditIdQueryBeforeResolver auditIdQueryBeforeResolver;
   private final LongParser longParser;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.UPDATE;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final var annotation = context.getAnnotation();
      final Object input = evaluator.evaluateAsObject(annotation.input(), context.getParamNames(), context.getArgs());

      final Long entityId = input instanceof AuthPrincipal principal ?
         entityIdBeforeResolver.getEntityIdByCurrentAuthentication(principal, annotation.entityType()) :
         longParser.parseString(
            evaluator.evaluate(annotation.entityId(), context.getParamNames(), context.getArgs())
         );

      context.setEntityId(entityId);
      context.setInput(auditIdQueryBeforeResolver.getStateBeforeOfById(annotation.entityType(), entityId));
   }
}
