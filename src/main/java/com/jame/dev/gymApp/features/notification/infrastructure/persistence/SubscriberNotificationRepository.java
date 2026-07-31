package com.jame.dev.gymApp.features.notification.infrastructure.persistence;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriberNotificationRepository extends JpaRepository<SubscriberNotificationEntity, UUID> {
    Optional<SubscriberNotificationEntity> findBySubscriptionId(final long subscriptionId);
    boolean existsBySubscription(final SubscriptionEntity subscription);
}
