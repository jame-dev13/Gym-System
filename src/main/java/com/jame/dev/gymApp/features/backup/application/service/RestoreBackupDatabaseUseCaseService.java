package com.jame.dev.gymApp.features.backup.application.service;

import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.backup.application.usecases.RestoreBackupDatabaseUseCase;
import com.jame.dev.gymApp.features.backup.domain.model.BackupStatus;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupMutationRepository;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupRestoreExecutor;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupFailureListener;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupSuccessListener;
import com.jame.dev.gymApp.features.backup.infrastructure.path.PathService;
import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestoreBackupDatabaseUseCaseService implements RestoreBackupDatabaseUseCase {
   private final BackupQueryRepository backupQueryRepository;
   private final BackupMutationRepository backupMutationRepository;
   private final BackupRestoreExecutor pgBackupRestoreRepository;
   private final HashExecutor hashExecutor;
   private final PathService pathService;

   @Override
   public void restore(final UUID backupId) {
      final var backupDocument = backupQueryRepository.findById(backupId)
         .orElseThrow(() -> new NotFoundException("Backup Document not found for: " + backupId));

      final BackupSuccessListener onSuccess = () -> {
         backupDocument.setBackupStatus(BackupStatus.RESTORED);
         backupMutationRepository.save(backupDocument);
      };

      final BackupFailureListener onFailure = () -> {
         backupDocument.setBackupStatus(BackupStatus.RESTORED_FAIL);
         backupMutationRepository.save(backupDocument);
      };

      final String fileName = backupDocument.getFileName();
      final Path destination = pathService.resolveBackupFilePath(fileName);
      final boolean checksumMatches = hashExecutor.verify(fileName, backupDocument.getChecksum());

      if (!checksumMatches) {
         throw new MissMatchException("File integrity compromised.");
      }

      pgBackupRestoreRepository.restore(destination, onSuccess, onFailure);
   }
}
