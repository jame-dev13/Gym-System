package com.jame.dev.gymApp.features.notification.application.usecases.mutation;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;

public interface ActivateSubscriberNotificationUseCase {
   SubscriberNotificationResponse activateNotification(final AuthPrincipal principal);
}
