package com.jame.dev.gymApp.features.audit.application.support.mapper;

import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper extends BaseMapper<AuditLogDocument, AuditLogResponse> {
   @Override
   AuditLogResponse toDto(final AuditLogDocument entity);

   default AuditLogDocument toEntity(AuditLogInput auditLogInput) {
      return AuditLogDocument.builder()
         .entity(auditLogInput.entity())
         .action(auditLogInput.auditLogAction())
         .actor(auditLogInput.actor())
         .payload(auditLogInput.payload())
         .success(auditLogInput.success())
         .metadata(auditLogInput.metadata())
         .build();
   }
}
