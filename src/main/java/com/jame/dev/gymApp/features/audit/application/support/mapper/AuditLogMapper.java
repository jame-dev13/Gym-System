package com.jame.dev.gymApp.features.audit.application.support.mapper;

import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import com.jame.dev.gymApp.features.audit.infrastructure.utils.AuditLogRetentionPolicy;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface AuditLogMapper extends BaseMapper<AuditLogDocument, AuditLogResponse> {
   @Override
   AuditLogResponse toDto(final AuditLogDocument entity);

   default AuditLogDocument toEntity(AuditLogInput auditLogInput) {
      final Instant createdAt = Instant.now();
      return AuditLogDocument.builder()
         .entity(auditLogInput.entity())
         .action(auditLogInput.auditLogAction())
         .actor(auditLogInput.actor())
         .payload(auditLogInput.payload())
         .kind(auditLogInput.auditLogKind())
         .success(auditLogInput.success())
         .metadata(auditLogInput.metadata())
         .createdAt(createdAt)
         .expiresAt(createdAt.plus(
               AuditLogRetentionPolicy.retentionFor(auditLogInput.auditLogKind())
            )
         )
         .build();
   }
}
