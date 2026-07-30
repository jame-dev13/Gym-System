package com.jame.dev.gymApp.features.notification.application.service.query;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.usecases.query.GetByIdSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationQueryRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.cache.SubscriberNotificationCacheValues;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess
public class GetByIdSubscriberNotificationUseCaseService implements GetByIdSubscriberNotificationUseCase {
   private final SubscriberNotificationQueryRepository subscriberNotificationQueryRepository;
   private final SubscriberNotificationFactory subscriberNotificationFactory;

   @Override
   @Cacheable(value = SubscriberNotificationCacheValues.VALUE, key = "uuid", unless = "#result == null")
   public SubscriberNotificationResponse getById(UUID uuid) {
      return subscriberNotificationQueryRepository.findById(uuid)
         .map(subscriberNotificationFactory::createFromEntity)
         .orElseThrow(() -> new NotFoundException("Subscriber notification not found for id: " + uuid));
   }
}
