package com.jame.dev.gymApp.features.auth.infrastructure.oauth2;

import com.jame.dev.gymApp.features.auth.infrastructure.annotation.VerifyOauthUser;
import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;

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
      return new CustomOAuth2User(authenticatedUser, oAuth2User.getAttributes(), authorities);
   }
}
