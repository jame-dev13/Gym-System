package com.jame.dev.gymApp.features.backup.application.usecases;

import java.util.UUID;

public interface DeleteBackupByIdUseCase {
   void deleteById(UUID uuid);
}
