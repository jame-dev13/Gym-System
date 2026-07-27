package com.jame.dev.gymApp.features.backup.application.service;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.application.support.factory.BackupFactory;
import com.jame.dev.gymApp.features.backup.application.usecases.GetBackupPageUseCase;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.cache.CacheBackupValues;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GetBackupPageUseCaseService implements GetBackupPageUseCase {
   private final BackupFactory backupFactory;
   private final BackupQueryRepository backupQueryRepository;

   @Override
   @Cacheable(
      value = CacheBackupValues.CACHE_BACKUP,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()",
      cacheManager = "redisCacheManager"
   )
   public PageDto<BackupResponse> getBackupPage(final Pageable pageable) {
      return backupFactory.createPageFrom(backupQueryRepository.findAll(pageable));
   }
}
