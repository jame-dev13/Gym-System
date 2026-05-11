package com.jame.dev.gymApp.features.auth.application.contract.session;

import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.features.auth.api.response.SessionResponse;
import org.springframework.security.core.Authentication;

public interface SessionService {
   SessionResponse getSession(
           @NotEmptyNull String access,
           @NotNullObject Authentication authentication);
}
