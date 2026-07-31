package com.jame.dev.gymApp.features.notification.infrastructure.adapter;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationQueryRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.persistence.SubscriberNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SubscriberNotificationQueryJpaRepositoryAdapter implements SubscriberNotificationQueryRepository {
   private final SubscriberNotificationRepository subscriberNotificationRepository;

   @Override
   public Optional<SubscriberNotificationEntity> findById(UUID uuid) {
      return subscriberNotificationRepository.findById(uuid);
   }

   @Override
   public Optional<SubscriberNotificationEntity> findBySubscriptionId(long subscriptionId) {
      return subscriberNotificationRepository.findBySubscriptionId(subscriptionId);
   }

   @Override
   public Optional<SubscriberNotificationEntity> findBySubscriberId(long subscriberId) {
      return subscriberNotificationRepository.findBySubscriptionId(subscriberId);
   }
}
