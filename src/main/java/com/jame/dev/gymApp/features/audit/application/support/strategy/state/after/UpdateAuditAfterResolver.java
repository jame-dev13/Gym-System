package com.jame.dev.gymApp.features.audit.application.support.strategy.state.after;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditAfterResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.parser.LongParser;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateAuditAfterResolver implements AuditAfterResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final LongParser longParser;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.UPDATE;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      if(context.getEntityId() == null) {
         final String idStr = evaluator.evaluate(context.getAnnotation().entityId(), context.getResult());
         context.setEntityId(longParser.parseString(idStr));
      }
      context.setResultValue(evaluator.evaluateAsObject(context.getAnnotation().result(), context.getResult()));
   }
}
