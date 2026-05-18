package com.jame.dev.gymApp.features.audit.api;


import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.application.contract.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/v1/administration/logs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AuditLogController {
   private final AuditLogService auditLogService;

   @GetMapping
   public ResponseEntity<Page<AuditLogResponse>> getAuditLogPage(
      @PageableDefault final Pageable pageable) {
      final PageDto<AuditLogResponse> pageDto = auditLogService.getPage(pageable);
      final Page<AuditLogResponse> page = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(page);
   }

   @GetMapping("/{id}")
   public ResponseEntity<AuditLogResponse> getAuditLogById(@PathVariable ObjectId id) {
      return ResponseEntity.ok(auditLogService.getById(id));
   }
}
