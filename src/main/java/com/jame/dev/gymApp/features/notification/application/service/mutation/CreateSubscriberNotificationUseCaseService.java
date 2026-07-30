package com.jame.dev.gymApp.features.notification.application.service.mutation;

import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.dto.SubscriberNotificationFactoryDtoInput;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.CreateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationValidationRepository;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CreateSubscriberNotificationUseCaseService implements CreateSubscriberNotificationUseCase {
   private final SubscriberNotificationMutationRepository subscriberNotificationMutationRepository;
   private final SubscriberNotificationValidationRepository subscriberNotificationValidationRepository;
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final SubscriberNotificationFactory subscriberNotificationFactory;

   @Override
   @Transactional
   public SubscriberNotificationResponse createSubscriberNotification(SubscriberNotificationRequest request) {
      final SubscriptionEntity subscription = subscriptionQueryRepository.findById(request.subscriptionId())
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for id: " + request.subscriptionId()));

      if (subscriberNotificationValidationRepository.existsBySubscriber(subscription)) {
         throw new NotificationException("A subscriber notification already exists for subscription id: " + request.subscriptionId());
      }

      final SubscriberNotificationEntity entity = subscriberNotificationFactory.createFromInput(
         new SubscriberNotificationFactoryDtoInput(subscription, request.rangeDays()));

      final SubscriberNotificationEntity saved = subscriberNotificationMutationRepository.save(entity);

      return subscriberNotificationFactory.createFromEntity(saved);
   }
}
