package com.jame.dev.gymApp.features.audit.application.support.strategy.state.before;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.support.resolver.AuditCurrentEntityIdBeforeResolver;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.parser.LongParser;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteAuditBeforeResolver implements AuditBeforeResolver {

   private final AuditLogExpressionEvaluator evaluator;
   private final AuditCurrentEntityIdBeforeResolver entityIdBeforeResolver;
   private final LongParser longParser;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.DELETE;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final Object input = evaluator.evaluateAsObject(context.getAnnotation().input(), context.getParamNames(), context.getArgs());
      final Long entityId = switch (input) {
         case AuthPrincipal principal ->
            entityIdBeforeResolver.getEntityIdByCurrentAuthentication(principal, context.getAnnotation().entityType());
         case null -> longParser.parseString(
            evaluator.evaluate(context.getAnnotation().entityId(), context.getParamNames(), context.getArgs())
         );
         default -> throw new IllegalStateException("Unexpected value: " + input);
      };
      context.setEntityId(entityId);
   }
}
