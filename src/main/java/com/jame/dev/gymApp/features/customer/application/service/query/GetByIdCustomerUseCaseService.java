package com.jame.dev.gymApp.features.customer.application.service.query;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetByIdCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetByIdCustomerUseCaseService implements GetByIdCustomerUseCase {
    private final CustomerQueryRepository customerQueryRepository;
    private final CustomerFactory customerFactory;

    @Override
    @Cacheable(value = CacheValues.CUSTOMER, key = "#id")
    public CustomerResponse getById(long id) {
        return customerQueryRepository.findById(id)
            .map(customerFactory::createFromEntity)
            .orElseThrow(() -> new CustomerNotFoundException("Customer Not found."));
    }
}
