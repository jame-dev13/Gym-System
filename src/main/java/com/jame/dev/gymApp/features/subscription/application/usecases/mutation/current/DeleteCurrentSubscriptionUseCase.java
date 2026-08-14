package com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current;

import org.springframework.security.core.Authentication;

public interface DeleteCurrentSubscriptionUseCase {
   void delete(final Authentication authentication);
}
