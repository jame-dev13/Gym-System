package com.jame.dev.gymApp.features.notification.application.usecases.mutation;

import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationUpdateRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;

import java.util.UUID;

public interface UpdateSubscriberNotificationUseCase {
   SubscriberNotificationResponse updateSubscriberNotification(final UUID uuid, final SubscriberNotificationUpdateRequest request);
}
