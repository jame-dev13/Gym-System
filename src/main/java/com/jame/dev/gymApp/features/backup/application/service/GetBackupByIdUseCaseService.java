package com.jame.dev.gymApp.features.backup.application.service;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.application.support.mapper.BackupMapper;
import com.jame.dev.gymApp.features.backup.application.usecases.GetBackupByIdUseCase;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.cache.CacheBackupValues;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBackupByIdUseCaseService implements GetBackupByIdUseCase {
   private final BackupQueryRepository backupQueryRepository;
   private final BackupMapper backupMapper;

   @Override
   @Cacheable(
      value = CacheBackupValues.CACHE_BACKUP,
      key = "#uuid",
      cacheManager = "redisCacheManager",
      unless = "#result == null"
   )
   public BackupResponse getById(UUID uuid) {
      return backupQueryRepository.findById(uuid)
         .map(backupMapper::toResponse)
         .orElseThrow(() -> new NotFoundException("Backup not found for: " + uuid));
   }
}
