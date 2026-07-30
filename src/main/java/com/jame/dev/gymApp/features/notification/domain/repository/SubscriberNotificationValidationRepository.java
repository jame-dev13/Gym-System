package com.jame.dev.gymApp.features.notification.domain.repository;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;

import java.util.UUID;

public interface SubscriberNotificationValidationRepository {

   boolean existsById(final UUID uuid);

   boolean existsBySubscriber(final SubscriptionEntity subscriptionEntity);
}
