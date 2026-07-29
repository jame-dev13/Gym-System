package com.jame.dev.gymApp.features.backup.domain.repository;

import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BackupQueryRepository {
   Page<BackupDocument> findAll(final Pageable pageable, final String search);

   Optional<BackupDocument> findById(final UUID uuid);
}
