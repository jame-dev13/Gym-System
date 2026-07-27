package com.jame.dev.gymApp.features.backup.application.dto;

import java.nio.file.Path;

public record BackupInput(
   Path filePath
) {
}
