package com.jame.dev.gymApp.features.notification.application.usecases.query;

import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;

import java.util.UUID;

@Deprecated
public interface GetByIdSubscriberNotificationUseCase {
   SubscriberNotificationResponse getById(final UUID uuid);
}