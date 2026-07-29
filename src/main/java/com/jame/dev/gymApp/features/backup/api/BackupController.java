package com.jame.dev.gymApp.features.backup.api;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.application.usecases.*;
import com.jame.dev.gymApp.features.backup.infrastructure.rate_limiting.BackupRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Slf4j
@RestController
@RequestMapping("/app/v1/administration/backups")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
public class BackupController {

   private final CreateDatabaseBackupUseCase createDatabaseBackupUseCase;
   private final GetBackupPageUseCase getBackupPageUseCase;
   private final GetBackupByIdUseCase getBackupByIdUseCase;
   private final RestoreBackupDatabaseUseCase restoreBackupDatabaseUseCase;
   private final GetBackupDumpFileUseCase getBackupDumpFileUseCase;
   private final BackupRateLimiter backupRateLimiter;

   @GetMapping
   public ResponseEntity<Page<BackupResponse>> getBackupPage(
      @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) final Pageable pageable,
      @RequestParam(value = "search", required = false) final String search
   ) {
      final PageDto<BackupResponse> pageDto = getBackupPageUseCase.getBackupPage(pageable, search);
      final Page<BackupResponse> page = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(page);
   }

   @GetMapping("/{id}")
   public ResponseEntity<BackupResponse> getBackup(
      @PathVariable final UUID id
   ) {
      return ResponseEntity.ok(getBackupByIdUseCase.getById(id));
   }

   @GetMapping("/{id}/download")
   public ResponseEntity<Resource> downloadBackup(
      @PathVariable("id") final UUID id
   ) throws IOException {
      log.info("[HIT]: /{id}/download");
      final Resource file = getBackupDumpFileUseCase.getResourceDumpFile(id);
      return ResponseEntity.ok()
         .header(
            CONTENT_DISPOSITION,
            ContentDisposition
               .attachment()
               .filename(file.getFilename())
               .build()
               .toString()
         )
         .contentLength(file.contentLength())
         .contentType(MediaType.APPLICATION_OCTET_STREAM)
         .body(file);
   }

   @PostMapping
   public ResponseEntity<Void> createDatabaseBackup(
      final HttpServletRequest request
   ) {
      backupRateLimiter.checkRateLimit(request);
      createDatabaseBackupUseCase.createDatabaseBackup();
      return ResponseEntity.accepted().build();
   }

   @PostMapping("/{id}/restore")
   public ResponseEntity<Void> restoreBackup(
      @PathVariable("id") final UUID uuid
   ) {
      restoreBackupDatabaseUseCase.restore(uuid);
      return ResponseEntity.accepted().build();
   }
}
