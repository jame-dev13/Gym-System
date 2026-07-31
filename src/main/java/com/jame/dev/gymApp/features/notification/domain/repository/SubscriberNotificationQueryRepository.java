package com.jame.dev.gymApp.features.notification.domain.repository;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;

import java.util.Optional;
import java.util.UUID;

public interface SubscriberNotificationQueryRepository {

   Optional<SubscriberNotificationEntity> findById(final UUID uuid);

   Optional<SubscriberNotificationEntity> findBySubscriptionId(final long subscriptionId);

   Optional<SubscriberNotificationEntity> findBySubscriberId(final long subscriberId);

}
