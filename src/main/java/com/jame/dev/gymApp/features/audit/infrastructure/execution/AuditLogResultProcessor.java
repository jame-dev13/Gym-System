package com.jame.dev.gymApp.features.audit.infrastructure.execution;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogResultProcessor {

   private final AuditLogExpressionEvaluator evaluator;

   public void processResultObject(final Object result, final AuditExecutionContext context) {
      context.setResult(result);
      final var resultExpr = context.getAnnotation().result();
      final Object resultValue = evaluator.evaluateAsObject(resultExpr, context.getResult());
      context.setResultValue(resultValue);
   }
}
