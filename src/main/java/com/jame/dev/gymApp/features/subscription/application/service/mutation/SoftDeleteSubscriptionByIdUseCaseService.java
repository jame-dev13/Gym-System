package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.SoftDeleteSubscriptionByIdUseCase;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jame.dev.gymApp.application.model.CacheValues.SUBSCRIPTION;
import static com.jame.dev.gymApp.application.model.CacheValues.SUBSCRIPTIONS;

@Service
@RequiredArgsConstructor
public class SoftDeleteSubscriptionByIdUseCaseService implements SoftDeleteSubscriptionByIdUseCase {
   private final SubscriptionMutationRepository subscriptionMutationRepository;

   @Override
   @Transactional
   @Caching(evict = {
      @CacheEvict(
         value = SUBSCRIPTIONS,
         allEntries = true,
         beforeInvocation = true,
         cacheManager = "redisCacheManager"),
      @CacheEvict(
         value = SUBSCRIPTION,
         key = "#id",
         cacheManager = "redisCacheManager"
      ),
      @CacheEvict(
         value = CacheValues.PAYMENTS,
         allEntries = true,
         cacheManager = "redisCacheManager",
         beforeInvocation = true
      )
   })
   @AuditLog(
      action = AuditLogAction.DELETE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      entityId = "#id"
   )
   public void softDeleteById(long id) {
      subscriptionMutationRepository.deleteById(id);
   }
}
