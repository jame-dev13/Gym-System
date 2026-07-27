package com.jame.dev.gymApp.features.backup.infrastructure.listener;

@FunctionalInterface
public interface BackupFailureListener {
   void onFailure();
}
