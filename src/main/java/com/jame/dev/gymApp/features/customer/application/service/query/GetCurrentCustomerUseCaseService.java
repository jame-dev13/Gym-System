package com.jame.dev.gymApp.features.customer.application.service.query;

import com.jame.dev.gymApp.features.auth.application.service.IdentityExtractorApplicationService;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMER;

@Service
@RequiredArgsConstructor
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetCurrentCustomerUseCaseService implements GetCurrentCustomerUseCase {
   private final CustomerQueryRepository customerQueryRepository;
   private final CustomerFactory customerFactory;
   private final IdentityExtractorApplicationService identityExtractorApplicationService;

   @Override
   @Cacheable(value = CUSTOMER, keyGenerator = "authCurrentKeyGen")
   public CustomerResponse getCurrent(Authentication authentication) {
      final String authenticatedUsername = identityExtractorApplicationService.extract(authentication);
      return customerQueryRepository.findByUserEmail(authenticatedUsername)
         .map(customerFactory::createFromEntity)
         .orElseThrow(() -> new NotFoundException("Customer Not found for: " + authenticatedUsername));
   }
}
