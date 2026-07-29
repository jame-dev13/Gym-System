package com.jame.dev.gymApp.features.backup.infrastructure.adapter;

import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.persistence.BackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository("mongoBackupQueryRepository")
@RequiredArgsConstructor
public class BackupQueryMongoRepositoryAdapter implements BackupQueryRepository {
   private final BackupRepository backupRepository;

   @Override
   public Page<BackupDocument> findAll(Pageable pageable, String search) {
      return backupRepository.search(pageable, search);
   }

   @Override
   public Optional<BackupDocument> findById(UUID uuid) {
      return backupRepository.findById(uuid);
   }
}
