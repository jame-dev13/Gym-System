package com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;

public interface DeleteCurrentSubscriptionUseCase {
   void delete(final AuthPrincipal principal);
}
