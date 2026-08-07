package com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.payload;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;

public interface AuditLogPayloadResolver {

   AuditLogAction action();

   AuditPayload resolve(final AuditExecutionContext ctx);

}
