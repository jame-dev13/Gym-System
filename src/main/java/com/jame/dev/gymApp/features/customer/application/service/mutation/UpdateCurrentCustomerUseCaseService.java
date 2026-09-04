package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerUpdateRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.annotations.EvictCurrentOnUpdateCustomer;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@CheckLockProcess
public class UpdateCurrentCustomerUseCaseService implements UpdateCurrentCustomerUseCase {
   private final CustomerQueryRepository customerQueryRepository;
   private final CustomerMutationRepository mutationRepository;
   private final CustomerUpdater customerUpdater;
   private final CustomerFactory customerFactory;

   @Override
   @Transactional
   @EvictCurrentOnUpdateCustomer
   @AuditLog(
      entityType = AuditLogEntityType.CUSTOMER,
      action = AuditLogAction.UPDATE,
      input = "#principal",
      result = "#result"
   )
   public CustomerResponse updateCurrent(AuthPrincipal principal, CustomerCurrentRequest request) {
      final String username = principal.username();
      final var input = new CustomerRequest(username, request.phoneContact());
      final var customer = customerQueryRepository.findByUserEmail(username)
         .orElseThrow(() -> new NotFoundException("Customer not found for: " + username));
      customerUpdater.apply(customer, input);
      final var customerUpdated = mutationRepository.save(customer);
      return customerFactory.createFromEntity(customerUpdated);
   }

   @Override
   @Transactional
   @EvictCurrentOnUpdateCustomer
   public CustomerResponse updateCurrent(AuthPrincipal principal, CustomerUpdateRequest updateRequest) {
      final long id = principal.id();
      final var customer = customerQueryRepository.findById(id)
         .orElseThrow(CustomerNotFoundException::new);
      customer.setPhoneContact(updateRequest.phoneContact());
      customer.setAddressInfo(updateRequest.customerAddressInfo());
      final var customerUpdated = mutationRepository.save(customer);
      return customerFactory.createFromEntity(customerUpdated);
   }
}
