package com.jame.dev.gymApp.features.backup.domain.exception;

public class BackupException extends RuntimeException {
   public BackupException(String message) {
      super(message);
   }

   public BackupException(String message, Throwable cause) {
      super(message, cause);
   }
}
