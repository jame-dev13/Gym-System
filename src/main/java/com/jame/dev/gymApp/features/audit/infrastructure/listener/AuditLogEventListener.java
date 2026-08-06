package com.jame.dev.gymApp.features.audit.infrastructure.listener;

import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogFactory;
import com.jame.dev.gymApp.features.audit.domain.event.AuditLogEvent;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import com.jame.dev.gymApp.features.audit.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogEventListener {

   private final AuditLogRepository auditLogRepository;
   private final AuditLogFactory auditLogFactory;

   @Async("taskExecutor")
   @EventListener(AuditLogEvent.class)
   @CacheEvict(value = "auditLogs", allEntries = true)
   public void processAuditLogEvent(AuditLogEvent auditLogEvent) {
      final AuditLogInput input = auditLogEvent.input();
      final AuditLogDocument entity = auditLogFactory.createFromInput(input);
      auditLogRepository.save(entity);
   }
}
