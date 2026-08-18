package com.jame.dev.gymApp.features.audit.application.usecases;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface GetAuditLogPageByCurrentUseCase {
   PageDto<AuditLogResponse> getPage(
      final Authentication authentication,
      final Pageable pageable,
      final String search);
}
