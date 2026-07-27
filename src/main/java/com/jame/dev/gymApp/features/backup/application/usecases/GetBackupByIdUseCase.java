package com.jame.dev.gymApp.features.backup.application.usecases;

import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;

import java.util.UUID;

public interface GetBackupByIdUseCase {

   BackupResponse getById(final UUID uuid);

}
