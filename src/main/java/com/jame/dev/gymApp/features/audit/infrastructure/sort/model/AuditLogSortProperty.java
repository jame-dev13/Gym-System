package com.jame.dev.gymApp.features.audit.infrastructure.sort.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuditLogSortProperty {
   ID("id", "id"),
   ACTION("action", "action"),
   ACTOR("actor", "actor.username"),
   ENTITY("entity", "entity.type"),
   CREATED_AT("createdAt", "createdAt");

   private final String apiProperty;
   private final String entityProperty;
}
