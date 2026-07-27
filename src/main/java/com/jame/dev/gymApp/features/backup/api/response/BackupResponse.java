package com.jame.dev.gymApp.features.backup.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.backup.domain.model.BackupStatus;

import java.util.UUID;

public record BackupResponse(
   @JsonProperty("id") UUID id,
   @JsonProperty("fileName") String fileName,
   @JsonProperty("size") long size,
   @JsonProperty("backupStatus") BackupStatus backupStatus,
   @JsonProperty("createdBy") String createdBy,
   @JsonProperty("createdAt") String createdAt
) {
}
