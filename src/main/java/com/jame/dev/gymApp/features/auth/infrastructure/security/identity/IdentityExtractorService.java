package com.jame.dev.gymApp.features.auth.infrastructure.security.identity;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import org.springframework.security.core.Authentication;

public interface IdentityExtractorService {
   String extract(final Authentication authentication);
   AuthPrincipal getContextPrincipal();
   CustomOAuth2User getOauthUser(Authentication authentication);
   UserPrincipal getUserPrincipal(Authentication authentication);
}
