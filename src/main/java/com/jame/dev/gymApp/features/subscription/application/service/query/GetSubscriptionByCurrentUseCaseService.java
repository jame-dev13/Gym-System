package com.jame.dev.gymApp.features.subscription.application.service.query;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetSubscriptionByCurrentUseCase;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jame.dev.gymApp.application.model.CacheValues.SUBSCRIPTION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetSubscriptionByCurrentUseCaseService implements GetSubscriptionByCurrentUseCase {
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final IdentityExtractorService identityExtractorService;
   private final SubscriptionMapper subscriptionMapper;

   @Override
   @Cacheable(
      value = SUBSCRIPTION,
      keyGenerator = "authCurrentKeyGen",
      unless = "#result == null"
   )
   public SubscriptionResponse getCurrent(Authentication authentication) {
      final String authName = identityExtractorService.extract(authentication);
      return subscriptionQueryRepository.findByCustomerEmail(authName)
         .map(subscriptionMapper::toDto)
         .orElseThrow(() -> new NotFoundException("Subscription not found for customer: " + authName));
   }
}
