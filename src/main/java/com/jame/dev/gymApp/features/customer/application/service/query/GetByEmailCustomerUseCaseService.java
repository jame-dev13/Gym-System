package com.jame.dev.gymApp.features.customer.application.service.query;

import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapper;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetByEmailCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetByEmailCustomerUseCaseService implements GetByEmailCustomerUseCase {
    private final CustomerQueryRepository customerQueryRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse getByEmail(String email) {
        return customerQueryRepository.findByUserEmail(email)
            .map(customerMapper::toDto)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));
    }
}
