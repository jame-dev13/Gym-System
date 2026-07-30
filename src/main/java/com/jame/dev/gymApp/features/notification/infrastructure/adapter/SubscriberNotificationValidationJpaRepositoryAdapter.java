package com.jame.dev.gymApp.features.notification.infrastructure.adapter;

import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationValidationRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.persistence.SubscriberNotificationRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SubscriberNotificationValidationJpaRepositoryAdapter implements SubscriberNotificationValidationRepository {
   private final SubscriberNotificationRepository subscriberNotificationRepository;

   @Override
   public boolean existsById(UUID uuid) {
      return subscriberNotificationRepository.existsById(uuid);
   }

   @Override
   public boolean existsBySubscriber(SubscriptionEntity subscriptionEntity) {
      return subscriberNotificationRepository.existsBySubscription(subscriptionEntity);
   }
}
