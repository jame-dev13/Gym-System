package com.jame.dev.gymApp.features.backup.domain.repository;

import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupFailureListener;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupSuccessListener;

import java.nio.file.Path;

public interface BackupDumpExecutor {

   void createBackup(
      final Path destination,
      final BackupSuccessListener resultListeners,
      final BackupFailureListener backupFailureListener);
}
