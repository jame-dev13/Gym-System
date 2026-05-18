package com.jame.dev.gymApp.features.audit.domain.event;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;

public record AuditLogEvent(
   AuditLogInput input
) {
}
