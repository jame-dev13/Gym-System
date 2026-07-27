package com.jame.dev.gymApp.features.backup.application.usecases;

import java.util.UUID;

public interface RestoreBackupDatabaseUseCase {

   void restore(final UUID backupId);

}
