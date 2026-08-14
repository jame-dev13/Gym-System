package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.application.service.IdentityExtractorApplicationService;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.dto.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.features.customer.application.support.validator.CustomerValidator;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.annotations.EvictOnSaveCustomers;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CreateCurrentCustomerUseCaseService implements CreateCurrentCustomerUseCase {
   private final CustomerMutationRepository mutationRepository;
   private final CustomerValidator customerValidator;
   private final IdentityExtractorApplicationService identityExtractorApplicationService;
   private final CustomerFactory customerFactory;

   @Override
   @Transactional
   @EvictOnSaveCustomers
   @AuditLog(
      entityType = AuditLogEntityType.CUSTOMER,
      action = AuditLogAction.INSERT,
      result = "#result",
      entityId = "#result.id",
      input = "#authentication"
   )
   public CustomerResponse createCurrent(final Authentication authentication, final CustomerCurrentRequest request) {
      final String authenticated = identityExtractorApplicationService.extract(authentication);
      final CustomerRequest input = new CustomerRequest(authenticated, request.phoneContact());
      final UserEntity userRelated = customerValidator.validateUserBeforeCreation(input);
      final CustomerEntity customer = mutationRepository.save(
         customerFactory.createFromInput(new CustomerFactoryDtoInput(userRelated, input))
      );

      return customerFactory.createFromEntity(customer);
   }
}
