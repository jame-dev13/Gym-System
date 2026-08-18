package com.jame.dev.gymApp.features.backup.application.support.factory;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.application.dto.BackupInput;
import com.jame.dev.gymApp.features.backup.application.support.mapper.BackupMapper;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.model.BackupStatus;
import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class BackupApplicationFactory implements BackupFactory {

   private final BackupMapper backupMapper;
   private final HashExecutor hashExecutor;
   private final IdentityExtractorService identityExtractor;

   @Override
   public PageDto<BackupResponse> createPageFrom(Page<BackupDocument> page) {
      return new PageDto<>(
         page.getContent().stream().map(backupMapper::toResponse).toList(),
         page.getNumber(),
         page.getSize(),
         page.getTotalElements(),
         page.getSort().toString(),
         page.getSort().isSorted() ? "ASC" : "DESC"
      );
   }

   @Override
   public BackupResponse createFromEntity(BackupDocument entity) {
      return backupMapper.toResponse(entity);
   }

   @Override
   public BackupDocument createFromInput(BackupInput input) {
      final String fileName = input.filePath().getFileName().toString();
      final File file = input.filePath().toFile();
      return BackupDocument.builder()
         .fileName(fileName)
         .size(file.exists() ? file.length() : -1)
         .checksum(hashExecutor.hash(fileName))
         .backupStatus(BackupStatus.PROGRESS)
         .createdBy(identityExtractor.extract(
            SecurityContextHolder
               .getContext()
               .getAuthentication()
            )
         )
         .build();
   }
}
