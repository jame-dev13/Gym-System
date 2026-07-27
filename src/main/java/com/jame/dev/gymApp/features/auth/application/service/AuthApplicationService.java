package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.features.auth.application.contract.AuthService;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.application.support.factory.AuthResponsesFactory;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationAttemptFailureException;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.CheckExistence;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.CheckSignIn;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishVerify;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import com.jame.dev.gymApp.infrastructure.cache.BlacklistService;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Validated
@CheckLockProcess
public class AuthApplicationService implements AuthService {
   private final UserRepository userRepository;
   private final UserFactory userFactory;
   private final AuthenticationManager authenticationManager;
   private final BlacklistService blacklistService;
   private final AuthResponsesFactory authFactory;

   @Override
   @Transactional
   @CheckExistence
   @PublishVerify
   public void signUp(final RegisterRequest register) {
      final UserRequest userRequest = UserRequest.builder()
         .name(register.name())
         .email(register.email())
         .password(register.password())
         .authProvider(AuthProvider.LOCAL)
         .roles(Set.of(Role.USER))
         .build();
      final UserEntity user = userFactory.createFromInput(userRequest);
      userRepository.saveAndFlush(user);
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
