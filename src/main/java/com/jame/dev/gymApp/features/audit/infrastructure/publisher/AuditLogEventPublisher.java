package com.jame.dev.gymApp.features.audit.infrastructure.publisher;

import com.jame.dev.gymApp.features.audit.domain.event.AuditLogEvent;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogEventPublisher {
   private final ApplicationEventPublisher eventPublisher;

   public void publishAuditLogEvent(final AuditLogInput input) {
      eventPublisher.publishEvent(new AuditLogEvent(input));
   }

}
