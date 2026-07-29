package com.jame.dev.gymApp.features.backup.infrastructure.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BackupSortProperty {
   ID("id", "id"),
   SIZE("size", "size"),
   STATUS("status", "backupStatus"),
   CREATED_AT("instant", "createdAt");

   private final String apiProperty;
   private final String entityProperty;
}
