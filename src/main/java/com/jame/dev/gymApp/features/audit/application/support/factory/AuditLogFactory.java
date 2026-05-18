package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.support.factories.Factory;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.application.support.mapper.AuditLogMapper;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuditLogFactory implements Factory<AuditLogDocument, AuditLogResponse, AuditLogInput> {
   private final AuditLogMapper auditLogMapper;

   @Override
   public PageDto<AuditLogResponse> createPageFrom(Page<AuditLogDocument> page) {
      final List<AuditLogResponse> content = page.getContent()
         .stream()
         .map(auditLogMapper::toDto)
         .toList();
      return new PageDto<>(
         content,
         page.getNumber(),
         page.getSize(),
         page.getTotalElements(),
         page.getSort().toString(),
         page.getSort().isSorted() ? "ASC": "DESC"
      );
   }

   @Override
   public AuditLogResponse createFromEntity(AuditLogDocument document) {
      return auditLogMapper.toDto(document);
   }

   @Override
   public AuditLogDocument createFromInput(AuditLogInput input) {
      return auditLogMapper.toEntity(input);
   }
}
