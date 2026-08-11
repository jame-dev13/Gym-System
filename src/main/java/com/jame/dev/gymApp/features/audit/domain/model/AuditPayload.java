package com.jame.dev.gymApp.features.audit.domain.model;

public sealed interface AuditPayload
   permits AuditLogCrudPayload, AuditLogAuthPayload {

   AuditPayloadType type();
}
