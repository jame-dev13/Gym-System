package com.jame.dev.gymApp.features.notification.application.service.mutation;

import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationUpdateRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.UpdateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationQueryRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.annotation.EvictSubscriberNotification;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class UpdateSubscriberNotificationUseCaseService implements UpdateSubscriberNotificationUseCase {
   private final SubscriberNotificationQueryRepository subscriberNotificationQueryRepository;
   private final SubscriberNotificationMutationRepository subscriberNotificationMutationRepository;
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final SubscriberNotificationFactory subscriberNotificationFactory;

   @Override
   @Transactional
   @EvictSubscriberNotification
   public SubscriberNotificationResponse updateSubscriberNotification(final UUID uuid, final SubscriberNotificationUpdateRequest request) {
      final SubscriberNotificationEntity entity = subscriberNotificationQueryRepository.findById(uuid)
         .orElseThrow(() -> new NotificationException("Subscriber notification not found for id: " + uuid));

      entity.setRangeNotificationDays(request.rangeDays());

      final SubscriberNotificationEntity saved = subscriberNotificationMutationRepository.save(entity);

      return subscriberNotificationFactory.createFromEntity(saved);
   }
}
