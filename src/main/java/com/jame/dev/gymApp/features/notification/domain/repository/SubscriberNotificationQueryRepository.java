package com.jame.dev.gymApp.features.notification.domain.repository;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;

import java.util.Optional;
import java.util.UUID;

public interface SubscriberNotificationQueryRepository {

   Optional<SubscriberNotificationEntity> findById(final UUID uuid);

   Optional<SubscriberNotificationEntity> findBySubscriber(final SubscriptionEntity subscriptionEntity);

}
