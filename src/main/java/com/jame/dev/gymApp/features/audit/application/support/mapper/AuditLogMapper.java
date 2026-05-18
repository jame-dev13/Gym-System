package com.jame.dev.gymApp.features.audit.application.support.mapper;

import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface AuditLogMapper extends BaseMapper<AuditLogDocument, AuditLogResponse> {
   @Override
   AuditLogResponse toDto(final AuditLogDocument entity);

   default String map(ObjectId value) {
      return value != null ? value.toHexString() : null;
   }

   default AuditLogDocument toEntity(AuditLogInput auditLogInput) {
      return AuditLogDocument.builder()
         .id(ObjectId.get())
         .entity(auditLogInput.entity())
         .action(auditLogInput.auditLogAction())
         .actor(auditLogInput.actor())
         .changes(auditLogInput.changes())
         .metadata(auditLogInput.metadata())
         .createdAt(Instant.now())
         .build();
   }
}
