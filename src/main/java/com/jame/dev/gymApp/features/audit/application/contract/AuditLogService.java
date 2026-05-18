package com.jame.dev.gymApp.features.audit.application.contract;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
   PageDto<AuditLogResponse> getPage(final Pageable pageable);
   AuditLogResponse getById(ObjectId id);
}
