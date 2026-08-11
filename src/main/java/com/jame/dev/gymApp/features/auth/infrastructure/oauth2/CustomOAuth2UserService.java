package com.jame.dev.gymApp.features.auth.infrastructure.oauth2;

import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.VerifyOauthUser;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
   private final CustomOauth2UserServiceHelper oauth2UserServiceHelper;

   @Override
   @VerifyOauthUser
   public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
      final OAuth2User oAuth2User = super.loadUser(userRequest);
      final String provider = userRequest.getClientRegistration().getRegistrationId();
      final AuthenticatedUser authenticatedUser = oauth2UserServiceHelper.handleUser(oAuth2User, provider);
      final Collection<GrantedAuthority> authorities = oauth2UserServiceHelper.getAuthoritiesFrom(authenticatedUser);
      log.debug("Returning CustomOAuth2User.");
      return new CustomOAuth2User(authenticatedUser, oAuth2User.getAttributes(), authorities);
   }
}
