package com.jame.dev.gymApp.features.customer.application.service.query;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetCurrentCustomerUseCaseService implements GetCurrentCustomerUseCase {
   private final CustomerQueryRepository customerQueryRepository;
   private final CustomerFactory customerFactory;

   @Override
   @Cacheable(value = CUSTOMER, keyGenerator = "authPrincipalCurrentKeyGen", unless = "#result == null")
   public CustomerResponse getCurrent(AuthPrincipal principal) {
      final String username = principal.username();
      return customerQueryRepository.findByUserEmail(username)
         .map(customerFactory::createFromEntity)
         .orElseThrow(() -> new NotFoundException("Customer Not found for: " + username));
   }
}
