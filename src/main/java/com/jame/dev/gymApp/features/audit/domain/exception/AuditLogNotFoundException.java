package com.jame.dev.gymApp.features.audit.domain.exception;

public class AuditLogNotFoundException extends RuntimeException {
   public AuditLogNotFoundException(String message) {
      super(message);
   }
}
