package com.jame.dev.gymApp.features.backup.application.service;

import com.jame.dev.gymApp.features.backup.application.dto.BackupInput;
import com.jame.dev.gymApp.features.backup.application.support.factory.BackupFactory;
import com.jame.dev.gymApp.features.backup.application.usecases.CreateDatabaseBackupUseCase;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.model.BackupStatus;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupDumpExecutor;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupMutationRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.annotations.EvictBackups;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupFailureListener;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupSuccessListener;
import com.jame.dev.gymApp.features.backup.infrastructure.path.PathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDatabaseBackupUseCaseService implements CreateDatabaseBackupUseCase {

   private final BackupDumpExecutor pgBackupRepository;
   private final PathService pathService;
   private final BackupMutationRepository mongoMutationBackupRepository;
   private final BackupFactory backupFactory;

   @Override
   @EvictBackups
   public void createDatabaseBackup() {
      final Path destinationPath = pathService.createBackupPath();
      final BackupInput input = new BackupInput(destinationPath);
      final BackupDocument backupDocument = mongoMutationBackupRepository.save(backupFactory.createFromInput(input));
      final BackupSuccessListener onSuccess = () -> {
         backupDocument.setSize(destinationPath.toFile().length());
         backupDocument.setBackupStatus(BackupStatus.SUCCESS);
         mongoMutationBackupRepository.save(backupDocument);
      };

      final BackupFailureListener onFailure = () -> {
         backupDocument.setBackupStatus(BackupStatus.FAILURE);
         mongoMutationBackupRepository.save(backupDocument);
      };

      pgBackupRepository.createBackup(destinationPath, onSuccess, onFailure);
   }
}
