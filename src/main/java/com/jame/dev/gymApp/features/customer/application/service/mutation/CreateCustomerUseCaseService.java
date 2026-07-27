package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.dto.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.features.customer.application.support.validator.CustomerValidator;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMERS;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CreateCustomerUseCaseService implements CreateCustomerUseCase {
   private final CustomerMutationRepository customerMutationRepository;
   private final CustomerValidator customerValidator;
   private final CustomerFactory customerFactory;

   @Override
   @Transactional
   @Caching(
      evict = {
         @CacheEvict(value = CUSTOMERS, allEntries = true, cacheManager = "redisCacheManager"),
         @CacheEvict(value = CacheEvolutionMetricsValues.JOINING_CUSTOMERS, allEntries = true, cacheManager = "redisCacheManager")
      }
   )
   @AuditLog(
      action = AuditLogAction.INSERT,
      entityType = AuditLogEntityType.CUSTOMER,
      entityId = "#result.id",
      input = "#request",
      result = "#result"
   )
   public CustomerResponse create(CustomerRequest request) {
      final UserEntity user = customerValidator.validateUserBeforeCreation(request);
      final CustomerEntity customerEntity = customerFactory
         .createFromInput(new CustomerFactoryDtoInput(user, request));
      final CustomerEntity customerSaved = customerMutationRepository.save(customerEntity);
      return customerFactory.createFromEntity(customerSaved);
   }
}
