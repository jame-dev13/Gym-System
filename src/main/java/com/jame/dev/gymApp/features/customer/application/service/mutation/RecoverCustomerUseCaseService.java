package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.RecoverCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMERS;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class RecoverCustomerUseCaseService implements RecoverCustomerUseCase {
   private final CustomerMutationRepository customerMutationRepository;
   private final CustomerFactory customerFactory;

   @Override
   @Transactional
   @CacheEvict(value = CUSTOMERS, allEntries = true)
   public CustomerResponse recover(RecoveryRequest request) {
      final CustomerEntity customerEntity = customerMutationRepository.recoverByUserEmail(request.email());
      return customerFactory.createFromEntity(customerEntity);
   }
}
