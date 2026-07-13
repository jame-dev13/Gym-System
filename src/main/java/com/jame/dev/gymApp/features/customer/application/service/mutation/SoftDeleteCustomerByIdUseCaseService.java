package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.SoftDeleteCustomerByIdUseCase;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.annotations.CacheEvictCustomers;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoftDeleteCustomerByIdUseCaseService implements SoftDeleteCustomerByIdUseCase {
   private final CustomerMutationRepository customerMutationRepository;

   @Override
   @Transactional
   @CacheEvictCustomers
   @Caching(
      evict = {
         @CacheEvict(value = CacheEvolutionMetricsValues.DOWNING_CUSTOMERS, allEntries = true, cacheManager = "redisCacheManager")
      }
   )
   @AuditLog(
      action = AuditLogAction.DELETE,
      entityType = AuditLogEntityType.CUSTOMER,
      entityId = "#id"
   )
   public void softDeleteById(long id) {
      customerMutationRepository.deleteById(id);
   }
}
