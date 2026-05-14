package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.features.auth.application.contract.AuthService;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.application.support.factory.AuthResponsesFactory;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthProviderNotAllowedException;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationAttemptFailureException;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.CheckExistence;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.CheckSignIn;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import com.jame.dev.gymApp.infrastructure.cache.BlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthApplicationService implements AuthService {
   private final UserService userService;
   private final AuthenticationManager authenticationManager;
   private final BlacklistService blacklistService;
   private final AuthResponsesFactory authFactory;

   @Override
   @CheckExistence
   public void signUp(final UserRequest dto) {
      if (dto.authProvider() != AuthProvider.LOCAL) {
         throw new AuthProviderNotAllowedException("Local authentication provider only.");
      }
      final UserResponse userResponse = userService.save(dto);
      Objects.requireNonNull(userResponse, "Something went wrong storing data.");
   }

   @Override
   @CheckSignIn
   public SignInResponse signIn(SignInRequest dto) {
      final UsernamePasswordAuthenticationToken token =
              new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

      final Authentication authentication = authenticationManager.authenticate(token);

      final UserPrincipal userAuthenticated = Optional.ofNullable((UserPrincipal) authentication.getPrincipal())
              .orElseThrow(() -> new AuthenticationAttemptFailureException("Can't authenticate User."));
      return authFactory.createSignInOkDtoFrom(userAuthenticated);
   }

   @Override
   public CookieResponse refresh(final String token) {
      blacklistService.blacklistToken(token);
      return authFactory.createRefreshCookieResponseFrom(token);
   }
}
