package com.jame.dev.gymApp.features.backup.infrastructure.adapter;

import com.jame.dev.gymApp.features.backup.domain.exception.BackupException;
import com.jame.dev.gymApp.features.backup.domain.model.BackupMapping;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupRestoreExecutor;
import com.jame.dev.gymApp.features.backup.infrastructure.annotations.EvictBackups;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupFailureListener;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupSuccessListener;
import com.jame.dev.gymApp.infrastructure.io.IOLogReader;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import com.jame.dev.gymApp.infrastructure.security.lock.LockProcess;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository("pgBackupRestoreRepository")
@RequiredArgsConstructor
public class PgBackupRestoreExecutorAdapter implements BackupRestoreExecutor {

   private final BackupMapping backupMapping;
   private final IOLogReader reader;

   private final List<String> baseCommand = new ArrayList<>();

   @PostConstruct
   public void init() {
      baseCommand.addAll(List.of(
         "pg_restore",
         "-h", backupMapping.host(),
         "-p", String.valueOf(backupMapping.port()),
         "-U", backupMapping.username(),
         "-d", backupMapping.database(),
         "--clean",
         "--if-exists",
         "--no-owner",
         "-v",
         "-j", "4"
      ));
   }

   @Override
   @Async("taskExecutor")
   @EvictBackups
   @LockProcess(processKey = LockKeys.PG_RESTORE)
   public void restore(
      final Path destination,
      final BackupSuccessListener successListener,
      final BackupFailureListener failureListener) {
      final var command = new ArrayList<>(baseCommand);
      command.add(destination.toString());

      try {
         final var processBuilder = new ProcessBuilder(command);
         processBuilder
            .environment()
            .putIfAbsent("PGPASSWORD", backupMapping.password());
         final var process = processBuilder.start();

         reader.logErrInputStream(process.getErrorStream());

         final var completed = process.waitFor(600, TimeUnit.SECONDS);
         processBuilder.environment().clear();

         if (!completed) {
            failureListener.onFailure();
            log.error("Process timeout reached.");
            process.destroy();
         }

         log.info("Process finalized with exit code: {}", process.exitValue());
         successListener.onSuccess();
      } catch (IOException | InterruptedException e) {
         failureListener.onFailure();
         Thread.currentThread().interrupt();
         throw new BackupException("Error trying to restore backup file: " + e.getMessage(), e);
      }
   }
}
