package com.jame.dev.gymApp.features.audit.infrastructure.listener;

import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogFactory;
import com.jame.dev.gymApp.features.audit.domain.event.AuditLogEvent;
import com.jame.dev.gymApp.features.audit.domain.repository.AuditLogRepository;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.EvictOnCreateAuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogEventListener {

   private final AuditLogRepository auditLogRepository;
   private final AuditLogFactory auditLogFactory;

   @Async("taskExecutor")
   @EventListener(AuditLogEvent.class)
   @EvictOnCreateAuditLog
   public void onAuditLogEventCreated(final AuditLogEvent e) {
      auditLogRepository.save(
         auditLogFactory.createFromInput(e.input())
      );
   }
}
