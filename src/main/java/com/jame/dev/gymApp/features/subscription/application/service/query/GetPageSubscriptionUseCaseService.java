package com.jame.dev.gymApp.features.subscription.application.service.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetPageSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.specification.SubscriptionSpecification;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetPageSubscriptionUseCaseService implements GetPageSubscriptionUseCase {
    private final SubscriptionQueryRepository subscriptionQueryRepository;
    private final SubscriptionFactory subscriptionFactory;
    private final SortPropertyResolver subSortAppResolver;

    @Override
    @Cacheable(
        value = CacheValues.SUBSCRIPTIONS,
        keyGenerator = "pageKeyGenerator",
        unless = "#result == null || #result.content.isEmpty()"
    )
    public PageDto<SubscriptionResponse> getPage(Pageable pageable, String search) {
        final Pageable pageableWrapped = subSortAppResolver.resolve(pageable);
        final Specification<SubscriptionEntity> spec = new SubscriptionSpecification(search);
        final Page<SubscriptionEntity> entityPage = subscriptionQueryRepository.findAll(spec, pageableWrapped);
        return subscriptionFactory.createPageFrom(entityPage);
    }
}
