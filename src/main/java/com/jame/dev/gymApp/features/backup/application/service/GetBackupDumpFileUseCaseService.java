package com.jame.dev.gymApp.features.backup.application.service;

import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.backup.application.usecases.GetBackupDumpFileUseCase;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.path.PathService;
import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetBackupDumpFileUseCaseService implements GetBackupDumpFileUseCase {
   private final BackupQueryRepository backupQueryRepository;
   private final PathService pathService;
   private final HashExecutor hashExecutor;

   @Override
   public Resource getResourceDumpFile(final UUID documentId) {
      log.info("Enter on GetBackupDumpFileUseCaseService");
      final var document = backupQueryRepository.findById(documentId)
         .orElseThrow(() -> new NotFoundException("Document not found for: " + documentId.toString()));

      final String fileName = document.getFileName();

      if(!hashExecutor.verify(fileName, document.getChecksum()))
         throw new MissMatchException("File integrity compromised.");

      final Path file = pathService.resolveBackupFilePath(fileName);
      log.info("File located: {}", file);
      return new FileSystemResource(file);
   }
}
