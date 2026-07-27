package com.jame.dev.gymApp.features.user.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues;
import com.jame.dev.gymApp.features.user.application.usecases.mutation.HardDeleteUserByIdUseCase;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.infrastructure.annotations.CacheEvictUsers;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class HardDeleteUserByIdUseCaseService implements HardDeleteUserByIdUseCase {
   private final UserMutationRepository userMutationRepository;

   @Override
   @Transactional
   @CacheEvictUsers
   @Caching(
      evict = {
         @CacheEvict(value = CacheEvolutionMetricsValues.DOWNING_CUSTOMERS, allEntries = true, cacheManager = "redisCacheManager")
      }
   )
   @AuditLog(
      action = AuditLogAction.HARD_DELETE,
      entityType = AuditLogEntityType.USER,
      entityId = "#id"
   )
   public void hardDeleteById(long id) {
      userMutationRepository.hardDeleteById(id);
   }
}
