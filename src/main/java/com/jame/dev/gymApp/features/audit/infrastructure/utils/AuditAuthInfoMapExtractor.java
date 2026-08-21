package com.jame.dev.gymApp.features.audit.infrastructure.utils;

import com.jame.dev.gymApp.features.audit.application.model.AuditAuthenticationResultValue;

import java.util.Map;

public final class AuditAuthInfoMapExtractor {

   enum Status {
      SUCCESS, FAILURE
   }

   public static Map<String, Object> extractInfoMapFrom(final AuditAuthenticationResultValue res) {
      return Map.of(
         "status", Status.SUCCESS,
         "desc", "Operation %s Completed with success".formatted(res.operation()),
         "performedBy", res.performedBy()
      );
   }

   private AuditAuthInfoMapExtractor() {
   }
}
