package com.jame.dev.gymApp.features.notification.domain.repository;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;

import java.util.UUID;

public interface SubscriberNotificationMutationRepository {

   SubscriberNotificationEntity save(final SubscriberNotificationEntity entity);

   void deleteById(final UUID uuid);

}
