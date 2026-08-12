package com.jame.dev.gymApp.features.auth.infrastructure.auth;

import org.springframework.security.core.Authentication;

public interface AuthenticationUserResolver {

   Long resolveUserId(Authentication authentication);
}
