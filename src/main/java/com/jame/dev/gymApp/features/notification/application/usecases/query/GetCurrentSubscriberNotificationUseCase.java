package com.jame.dev.gymApp.features.notification.application.usecases.query;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;

public interface GetCurrentSubscriberNotificationUseCase {
   SubscriberNotificationResponse getCurrent(final AuthPrincipal principal);
}
