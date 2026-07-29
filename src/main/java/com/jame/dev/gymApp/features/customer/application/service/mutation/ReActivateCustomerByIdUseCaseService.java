package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.features.customer.application.usecases.mutation.ReActivateCustomerByIdUseCase;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMERS;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class ReActivateCustomerByIdUseCaseService implements ReActivateCustomerByIdUseCase {
    private final CustomerQueryRepository customerQueryRepository;
    private final CustomerMutationRepository customerMutationRepository;

    @Override
    @Transactional
    @CacheEvict(value = CUSTOMERS, allEntries = true)
    public void reActivateById(long id) {
        final CustomerEntity customer = customerQueryRepository.findDeactivatedById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found, id: " + id));
        customer.setActive(true);
        customer.setUpdatedAt(Instant.now());
        customerMutationRepository.save(customer);
    }
}
