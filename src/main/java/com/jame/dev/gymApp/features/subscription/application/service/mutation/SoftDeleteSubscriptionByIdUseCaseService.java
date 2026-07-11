package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.SoftDeleteSubscriptionByIdUseCase;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.CacheEvictSubscriptions;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoftDeleteSubscriptionByIdUseCaseService implements SoftDeleteSubscriptionByIdUseCase {
   private final SubscriptionMutationRepository subscriptionMutationRepository;

   @Override
   @Transactional
   @CacheEvictSubscriptions
   @Caching(
      evict = {
         @CacheEvict(
            value = CacheValues.PAYMENTS,
            allEntries = true,
            cacheManager = "redisCacheManager",
            beforeInvocation = true
         ),
         @CacheEvict(
            value = CacheEvolutionMetricsValues.DOWNING_SUBSCRIBERS, allEntries = true, cacheManager = "redisCacheManager"
         )
      }
   )
   @AuditLog(
      action = AuditLogAction.DELETE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      entityId = "#id"
   )
   public void softDeleteById(long id) {
      subscriptionMutationRepository.deleteById(id);
   }
}
