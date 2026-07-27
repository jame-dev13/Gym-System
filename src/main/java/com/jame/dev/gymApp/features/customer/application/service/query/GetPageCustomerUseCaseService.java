package com.jame.dev.gymApp.features.customer.application.service.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetPageCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.specification.CustomerSpecification;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMERS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetPageCustomerUseCaseService implements GetPageCustomerUseCase {
    private final CustomerQueryRepository customerQueryRepository;
    private final CustomerFactory customerFactory;
    private final SortPropertyResolver customerSortAppResolver;

    @Override
    @Cacheable(
        value = CUSTOMERS,
        keyGenerator = "pageKeyGenerator",
        unless = "#result == null || #result.content.isEmpty()"
    )
    public PageDto<CustomerResponse> getPage(Pageable pageable, String search) {
        final Pageable pageableWrapped = customerSortAppResolver.resolve(pageable);
        final Specification<CustomerEntity> spec = new CustomerSpecification(search);
        final Page<CustomerEntity> page = customerQueryRepository.findAll(spec, pageableWrapped);
        return customerFactory.createPageFrom(page);
    }
}
