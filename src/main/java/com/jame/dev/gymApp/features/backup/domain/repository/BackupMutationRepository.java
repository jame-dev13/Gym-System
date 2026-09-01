package com.jame.dev.gymApp.features.backup.domain.repository;

import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;

import java.util.UUID;

public interface BackupMutationRepository {
   BackupDocument save(BackupDocument backupDocument);

   void deleteBackupById(UUID uuid);
}
