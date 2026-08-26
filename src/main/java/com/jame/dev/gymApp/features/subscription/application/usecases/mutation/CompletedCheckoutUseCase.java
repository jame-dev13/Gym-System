package com.jame.dev.gymApp.features.subscription.application.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.application.dto.CompletedCheckoutResult;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;

public interface CompletedCheckoutUseCase {
    CompletedCheckoutResult execute(CompletedCheckoutEvent event);
}
