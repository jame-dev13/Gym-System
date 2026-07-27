package com.jame.dev.gymApp.features.subscription.application.service.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetAllSubscriptionsByCustomerEmailUseCase;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetAllSubscriptionsByCustomerEmailUseCaseService implements GetAllSubscriptionsByCustomerEmailUseCase {
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final SubscriptionFactory subscriptionFactory;

   @Override
   @Cacheable(
      value = CacheValues.SUBSCRIPTIONS,
      keyGenerator = "pageKeyGenerator",
      unless = "#result.content().isEmpty()"
   )
   public PageDto<SubscriptionResponse> getAllByCustomerEmail(final String customerEmail, final Pageable pageable) {
      final var entityPage = subscriptionQueryRepository.findAllByCustomerEmail(customerEmail, pageable);
      return subscriptionFactory.createPageFrom(entityPage);
   }
}
