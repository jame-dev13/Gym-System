package com.jame.dev.gymApp.features.notification.application.usecases.mutation;

import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;

public interface CreateSubscriberNotificationUseCase {
   SubscriberNotificationResponse createSubscriberNotification(final SubscriberNotificationRequest request);
}
