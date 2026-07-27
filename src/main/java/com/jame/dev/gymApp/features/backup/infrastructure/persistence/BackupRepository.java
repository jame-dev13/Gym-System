package com.jame.dev.gymApp.features.backup.infrastructure.persistence;

import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface BackupRepository extends MongoRepository<BackupDocument, UUID> {
}
