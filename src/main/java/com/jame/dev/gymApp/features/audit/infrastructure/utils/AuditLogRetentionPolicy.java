package com.jame.dev.gymApp.features.audit.infrastructure.utils;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogKind;

import java.time.Duration;

public final class AuditLogRetentionPolicy {
   private AuditLogRetentionPolicy(){}

   public static Duration retentionFor(AuditLogKind kind) {
      return switch (kind) {
         case AUTH -> Duration.ofDays(180);
         case ERROR -> Duration.ofDays(120);
         case SYSTEM, EXTERNAL -> Duration.ofDays(90);
      };
   }
}
