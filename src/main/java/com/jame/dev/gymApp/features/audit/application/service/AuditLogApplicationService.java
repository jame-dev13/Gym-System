package com.jame.dev.gymApp.features.audit.application.service;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.application.contract.AuditLogService;
import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogFactory;
import com.jame.dev.gymApp.features.audit.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogApplicationService implements AuditLogService {
   private final AuditLogRepository auditLogRepository;
   private final AuditLogFactory auditLogFactory;

   @Override
   @Cacheable(
      value = "auditLogs",
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()",
      cacheManager = "redisCacheManager"
   )
   public PageDto<AuditLogResponse> getPage(Pageable pageable) {
      return auditLogFactory.createPageFrom(auditLogRepository.findAll(pageable));
   }
}
