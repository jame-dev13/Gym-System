package com.jame.dev.gymApp.features.backup.infrastructure.path;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.backup.domain.exception.BackupException;
import com.jame.dev.gymApp.features.backup.domain.model.BackupMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class PathService {

   private final BackupMapping backupMapping;

   public Path createBackupPath() {
      final String timestamp = LocalDateTime.now()
         .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      final String filename = String.format("%s_%s.dump", backupMapping.database(), timestamp);
      final Path dir = Path.of(backupMapping.directory());
      log.info("Destination backup directory {}", dir);
      try {
         Files.createDirectories(dir);
      } catch (IOException e) {
         throw new BackupException("Cannot create backup directory: " + dir, e);
      }
      return dir.resolve(filename);
   }

   public Path resolveBackupFilePath(final String fileName) {
      final Path dir = Path.of(backupMapping.directory());
      log.info("Destination restore directory {}", dir);
      final boolean dirExists = Files.exists(dir);
      if(!dirExists)
         throw new NotFoundException("Backup Directory not found.");
      return dir.resolve(fileName);
   }
}
