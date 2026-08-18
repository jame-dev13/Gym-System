package com.jame.dev.gymApp.features.audit.application.service;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.application.contract.AuditLogService;
import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogFactory;
import com.jame.dev.gymApp.features.audit.domain.repository.AuditLogRepository;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogApplicationService implements AuditLogService {
   private final AuditLogRepository auditLogRepository;
   private final AuditLogFactory auditLogFactory;
   private final SortPropertyResolver auditLogSortAppResolver;

   @Override
   @Cacheable(
      value = "auditLogs",
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<AuditLogResponse> getPage(Pageable pageable, String search) {
      final var pageableResolved = auditLogSortAppResolver.resolve(pageable);
      return auditLogFactory.createPageFrom(auditLogRepository.search(pageableResolved, search));
   }
}
