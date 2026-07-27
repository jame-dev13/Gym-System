package com.jame.dev.gymApp.features.backup.domain.repository;

import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;

public interface BackupMutationRepository {
   BackupDocument save(BackupDocument backupDocument);
}
