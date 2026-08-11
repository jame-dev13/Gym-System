package com.jame.dev.gymApp.features.audit.application.model;

import lombok.Builder;

@Builder
public record AuditAuthenticationResultValue(
   Long userId, String performedBy, String operation
) {
}
