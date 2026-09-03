package com.jame.dev.gymApp.features.subscription.infrastructure.notification.usecases;

import com.jame.dev.gymApp.features.subscription.api.response.NotificationAvailabilityResponse;

public interface CheckNotificationAvailabilityUseCase {
   NotificationAvailabilityResponse checkAvailability();
}
