package com.jame.dev.gymApp.features.audit.application.service;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogFactory;
import com.jame.dev.gymApp.features.audit.application.usecases.GetAuditLogPageByCurrentUseCase;
import com.jame.dev.gymApp.features.audit.infrastructure.cache.AuditCacheValues;
import com.jame.dev.gymApp.features.audit.infrastructure.specification.AuditLogSpecifications;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAuditLogPageByCurrentUCService implements GetAuditLogPageByCurrentUseCase {
   private final AuditLogSpecifications auditLogSpecifications;
   private final AuditLogFactory auditLogFactory;
   private final IdentityExtractorService identityExtractorService;
   private final SortPropertyResolver auditLogSortAppResolver;

   @Override
   @Cacheable(
      value = AuditCacheValues.AUDIT_LOG_CURR_VAL,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<AuditLogResponse> getPage(Authentication authentication, Pageable pageable, String search) {
      final var pageableWrapped = auditLogSortAppResolver.resolve(pageable);
      final String actor = identityExtractorService.extract(authentication);
      return auditLogFactory.createPageFrom(
         auditLogSpecifications.findAllByCurrentActor(pageableWrapped, actor, search)
      );
   }
}
