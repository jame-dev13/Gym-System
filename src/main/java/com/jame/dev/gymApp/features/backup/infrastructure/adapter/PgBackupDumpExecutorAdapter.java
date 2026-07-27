package com.jame.dev.gymApp.features.backup.infrastructure.adapter;

import com.jame.dev.gymApp.features.backup.domain.exception.BackupException;
import com.jame.dev.gymApp.features.backup.domain.model.BackupMapping;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupDumpExecutor;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Repository("pgBackupRepository")
@RequiredArgsConstructor
public class PgBackupDumpExecutorAdapter implements BackupDumpExecutor {

   private final BackupMapping backupMapping;
   private final IOLogReader reader;

   private final List<String> baseCommand = new ArrayList<>();

   @PostConstruct
   public void init() {
      baseCommand.addAll(
         List.of(
            "pg_dump",
            "-h", backupMapping.host(),
            "-p", String.valueOf(backupMapping.port()),
            "-U", backupMapping.username(),
            "-d", backupMapping.database(),
            "-Fc"
         )
      );
   }

   @Override
   @Async("taskExecutor")
   @EvictBackups
   @LockProcess(processKey = LockKeys.PG_DUMP)
   public void createBackup(
      final Path destination,
      final BackupSuccessListener successListener,
      final BackupFailureListener failureListener) {

      final List<String> command = new ArrayList<>(baseCommand);
      try {
         final ProcessBuilder processBuilder = new ProcessBuilder(command);
         processBuilder
            .environment()
            .putIfAbsent("PGPASSWORD", backupMapping.password());
         final Process process = processBuilder.start();

         try (final InputStream in = process.getInputStream()) {
            Files.copy(in, destination, REPLACE_EXISTING);
         }

         reader.logErrInputStream(process.getErrorStream());

         final boolean completed = process.waitFor(600, TimeUnit.SECONDS);
         processBuilder.environment().clear();
         if (!completed) {
            log.info("Process timeout reached.");
            failureListener.onFailure();
            process.destroy();
         }

         log.info("Success reached.");
         successListener.onSuccess();
      } catch (IOException | InterruptedException e) {
         failureListener.onFailure();
         Thread.currentThread().interrupt();
         throw new BackupException("Cannot complete backup: " + e.getMessage(), e);
      }
   }
}
