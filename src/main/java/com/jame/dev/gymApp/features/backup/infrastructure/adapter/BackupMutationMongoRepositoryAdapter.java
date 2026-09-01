package com.jame.dev.gymApp.features.backup.infrastructure.adapter;

import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupMutationRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.persistence.BackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("mongoMutationBackupRepository")
@RequiredArgsConstructor
public class BackupMutationMongoRepositoryAdapter implements BackupMutationRepository {
   private final BackupRepository backupRepository;

   @Override
   public BackupDocument save(BackupDocument backupDocument) {
      return backupRepository.save(backupDocument);
   }

   @Override
   public void deleteBackupById(UUID uuid) {
      backupRepository.deleteById(uuid);
   }
}
