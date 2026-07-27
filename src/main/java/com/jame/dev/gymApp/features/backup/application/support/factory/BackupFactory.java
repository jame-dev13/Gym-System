package com.jame.dev.gymApp.features.backup.application.support.factory;

import com.jame.dev.gymApp.application.support.factories.Factory;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.application.dto.BackupInput;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;

public interface BackupFactory extends Factory<BackupDocument, BackupResponse, BackupInput> {
}
