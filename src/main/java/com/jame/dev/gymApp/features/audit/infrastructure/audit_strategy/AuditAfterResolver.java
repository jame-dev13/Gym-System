package com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;

public interface AuditAfterResolver {

   AuditLogAction action();

   void resolve(final AuditExecutionContext context);

}
