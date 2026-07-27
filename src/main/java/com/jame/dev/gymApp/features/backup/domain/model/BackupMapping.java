package com.jame.dev.gymApp.features.backup.domain.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.backup.db.command")
public record BackupMapping(
   String host,
   int port,
   String username,
   String password,
   String database,
   String directory
) {
}
