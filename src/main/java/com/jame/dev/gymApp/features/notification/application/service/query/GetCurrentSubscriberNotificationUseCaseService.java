package com.jame.dev.gymApp.features.notification.application.service.query;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.usecases.query.GetCurrentSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.infrastructure.annotation.CheckSubscriber;
import com.jame.dev.gymApp.features.notification.infrastructure.cache.SubscriberNotificationCacheValues;
import com.jame.dev.gymApp.features.notification.infrastructure.query.SubscriberNotifiableRetrieverRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess
public class GetCurrentSubscriberNotificationUseCaseService implements GetCurrentSubscriberNotificationUseCase {
   private final SubscriberNotifiableRetrieverRepository subscriberNotifiableRetrieverRepository;
   private final SubscriberNotificationFactory subscriberNotificationFactory;

   @Override
   @CheckSubscriber
   @Cacheable(
      value = SubscriberNotificationCacheValues.VALUE,
      keyGenerator = "authPrincipalCurrentKeyGen",
      unless = "#result == null"
   )
   public SubscriberNotificationResponse getCurrent(final AuthPrincipal principal) {
      final String username = principal.username();
      final var notificationEntity = subscriberNotifiableRetrieverRepository.findByCurrentUsername(username)
         .orElseThrow(() -> new NotFoundException("Notification record not found for: " + username));

      return subscriberNotificationFactory.createFromEntity(notificationEntity);
   }
}
