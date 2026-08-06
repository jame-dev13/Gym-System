package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogContext;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogContextFactory {

   AuditLogContext create(final AuditLog annotation) {
      return new AuditLogContext(annotation.action(), annotation.entityType());
   }

}
