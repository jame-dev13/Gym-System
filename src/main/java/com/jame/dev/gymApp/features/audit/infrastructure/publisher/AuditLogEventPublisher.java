package com.jame.dev.gymApp.features.audit.infrastructure.publisher;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogInputFactory;
import com.jame.dev.gymApp.features.audit.domain.event.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogEventPublisher {
   private final AuditLogInputFactory auditLogInputFactory;
   private final ApplicationEventPublisher eventPublisher;

   public void publishAuditLogEventSafely(final AuditExecutionContext context) {
      try {
         final var input = auditLogInputFactory.create(context);
         eventPublisher.publishEvent(new AuditLogEvent(input));
      } catch (final Exception ex) {
         log.error("Failed to publish input payload.", ex);
      }
   }

}
