package com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.entity;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;

public interface AuditActionBindResolver {

   AuditLogAction action();

   void resolveIdentity(final AuditExecutionContext context);

}
