package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.annotations.CacheEvictCustomers;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class UpdateCustomerUseCaseService implements UpdateCustomerUseCase {
    private final CustomerQueryRepository customerQueryRepository;
    private final CustomerMutationRepository customerMutationRepository;
    private final CustomerUpdater customerUpdater;
    private final CustomerFactory customerFactory;

    @Override
    @Transactional
    @CacheEvictCustomers
    @AuditLog(
        action = AuditLogAction.UPDATE,
        entityType = AuditLogEntityType.CUSTOMER,
        entityId = "#id",
        input = "#request",
        result = "#result"
    )
    public CustomerResponse update(long id, CustomerRequest request) {
        final CustomerEntity customer = customerQueryRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found, id: " + id));
        customerUpdater.apply(customer, request);
        final CustomerEntity customerSaved = customerMutationRepository.save(customer);
        return customerFactory.createFromEntity(customerSaved);
    }
}
