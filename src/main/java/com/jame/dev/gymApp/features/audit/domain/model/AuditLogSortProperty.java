package com.jame.dev.gymApp.features.audit.domain.model;

import lombok.Getter;

@Getter
public enum AuditLogSortProperty {
   ID("id", "id"),
   ACTION("action", "action"),
   ENTITY("entity", "entity.type"),
   CREATED_AT("createdAt", "createdAt");

   private final String apiProperty;
   private final String entityProperty;

   AuditLogSortProperty(String apiProperty, String entityProperty) {
      this.apiProperty = apiProperty;
      this.entityProperty = entityProperty;
   }
}
