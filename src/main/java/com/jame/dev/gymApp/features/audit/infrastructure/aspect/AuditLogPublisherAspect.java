package com.jame.dev.gymApp.features.audit.infrastructure.aspect;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogChanges;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogEntity;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogMetadata;
import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogChangesFactory;
import com.jame.dev.gymApp.features.audit.application.support.helper.ExtractAuditLogActorHelper;
import com.jame.dev.gymApp.features.audit.application.support.helper.ExtractAuditLogMetadataHelper;
import com.jame.dev.gymApp.features.audit.application.support.resolver.AuditIdQueryBeforeResolver;
import com.jame.dev.gymApp.features.audit.domain.event.AuditLogEvent;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(1)
public class AuditLogPublisherAspect {
   private final ApplicationEventPublisher applicationEventPublisher;
   private final AuditLogExpressionEvaluator evaluator;
   private final AuditIdQueryBeforeResolver auditIdQueryBeforeResolver;

   @Around(
      value = "@annotation(com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog)"
   )
   public Object publishAuditLog(ProceedingJoinPoint joinPoint) throws Throwable {
      var signature = (MethodSignature) joinPoint.getSignature();
      var method = signature.getMethod();
      var auditLog = method.getAnnotation(AuditLog.class);
      if (auditLog == null)
         return joinPoint.proceed();

      var paramNames = signature.getParameterNames();
      var args = joinPoint.getArgs();

      final AuditLogAction action = auditLog.action();
      final AuditLogEntityType entityType = auditLog.entityType();

      String entityIdStr;
      long entityId = Long.MIN_VALUE;
      Object input = null;

      switch (action) {
         case UPDATE -> {
            entityIdStr = evaluator.evaluate(auditLog.entityId(), paramNames, args);
            entityId = parseEntityId(entityIdStr);
            input = auditIdQueryBeforeResolver.getStateBeforeOfById(entityType, entityId);
         }
         case INSERT -> input = evaluator.evaluateAsObject(auditLog.input(), paramNames, args);
         case DELETE, RECOVER, HARD_DELETE -> {
            entityIdStr = evaluator.evaluate(auditLog.entityId(), paramNames, args);
            entityId = parseEntityId(entityIdStr);
            input = evaluator.evaluateAsObject(auditLog.input(), paramNames, args);
         }
      }

      Object result = joinPoint.proceed();

      try {
         if (action == AuditLogAction.INSERT) {
            entityIdStr = evaluator.evaluate(auditLog.entityId(), result);
            entityId = parseEntityId(entityIdStr);
         }
         final Object resultValue = evaluator.evaluateAsObject(auditLog.result(), result);
         final AuditLogEntity entity = new AuditLogEntity(entityType, entityId);
         final AuditLogActor actor = ExtractAuditLogActorHelper.extractLogActor();
         final AuditLogChanges changes = AuditLogChangesFactory.createAuditLogChangesFrom(action, entityId, input, resultValue);
         final AuditLogMetadata metadata = ExtractAuditLogMetadataHelper.extractAuditLogMetadata();
         final AuditLogInput auditLogInput = new AuditLogInput(entity, action, actor, changes, metadata);
         applicationEventPublisher.publishEvent(new AuditLogEvent(auditLogInput));
      } catch (Exception e) {
         log.warn("Cannot publish event {}", e.getMessage());
      }
      return result;
   }

   private long parseEntityId(String id) {
      long idParsed = Long.MIN_VALUE;
      try {
         idParsed = Long.parseLong(id);
      } catch (NumberFormatException e) {
         log.warn("Failed to parse entityId '{}' as long", id);
      }
      return idParsed;
   }

}
