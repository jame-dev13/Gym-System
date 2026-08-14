package com.jame.dev.gymApp.features.subscription.application.usecases.query;

import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import org.springframework.security.core.Authentication;

public interface GetSubscriptionByCurrentUseCase {
   SubscriptionResponse getCurrent(final Authentication authentication);
}
