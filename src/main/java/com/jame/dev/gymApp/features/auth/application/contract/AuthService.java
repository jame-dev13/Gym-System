package com.jame.dev.gymApp.features.auth.application.contract;

import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;

public interface AuthService {
   void signUp(final RegisterRequest register);

   SignInResponse signIn(@NotNullObject final SignInRequest dto);

   CookieResponse refresh(@NotEmptyNull final String token);
}
