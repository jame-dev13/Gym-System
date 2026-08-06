package com.jame.dev.gymApp.features.audit.application.support.strategy;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.parser.LongParser;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditAfterResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsertAuditAfterResolver implements AuditAfterResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final LongParser longParser;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.INSERT;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      log.debug("entityId: {}", context.getAnnotation().entityId());
      log.debug("param Names: {}", Arrays.stream(context.getParamNames()).toList());
      log.debug("args: {}", Arrays.stream(context.getArgs()).toList());

      final String idStr = evaluator.evaluate(context.getAnnotation().entityId(), context.getResult());
      final Long id = longParser.parseString(idStr);

      context.setEntityId(id);
   }
}
