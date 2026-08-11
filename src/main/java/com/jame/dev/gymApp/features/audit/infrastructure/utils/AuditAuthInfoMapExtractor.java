package com.jame.dev.gymApp.features.audit.infrastructure.utils;

import com.jame.dev.gymApp.features.audit.application.model.AuditAuthenticationResultValue;

import java.util.Map;

public final class AuditAuthInfoMapExtractor {

   enum Status {
      SUCCESS, FAILURE
   }

   public static Map<String, Object> extractInfoMapFrom(final Throwable th, final AuditAuthenticationResultValue res) {
      if (th != null) {
         return Map.of(
            "status", Status.FAILURE,
            "desc", th.getMessage(),
            "performedBy", res.performedBy());
      }

      return Map.of(
         "status", Status.SUCCESS,
         "desc", "Operation %s Completed with success".formatted(res.operation()),
         "performedBy", res.performedBy()
      );
   }

   private AuditAuthInfoMapExtractor() {
   }
}
