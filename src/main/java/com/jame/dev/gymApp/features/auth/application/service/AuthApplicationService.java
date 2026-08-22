package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.features.auth.application.contract.AuthService;
import com.jame.dev.gymApp.features.auth.application.support.factory.AuthResponsesFactory;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationAttemptFailureException;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.RegisterFlow;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.infrastructure.cache.BlacklistService;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
@CheckLockProcess
public class AuthApplicationService implements AuthService {
   private final UserMutationRepository userRepository;
   private final UserFactory userFactory;
   private final AuthenticationManager authenticationManager;
   private final BlacklistService blacklistService;
   private final AuthResponsesFactory authFactory;

   @Override
   @Transactional
   @RegisterFlow
   @AuditLog(
      input = "#register",
      action = AuditLogAction.REGISTER,
      entityType = AuditLogEntityType.AUTHENTICATION
   )
   public boolean signUp(final RegisterRequest register) {
      final var userEntity = userFactory.fromRegister(register);

      final boolean present = Optional.ofNullable(userRepository.save(userEntity))
         .isPresent();

      if (!present) {
         throw new NoSuchElementException("Something went wrong while processing user.");
      }

      return Boolean.TRUE;
   }

   @Override
   @AuditLog(
      input = "#dto",
      action = AuditLogAction.SIGN_IN,
      entityType = AuditLogEntityType.AUTHENTICATION,
      result = "#result"
   )
   public SignInResponse signIn(final SignInRequest dto) {
      final UsernamePasswordAuthenticationToken token =
         new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

      final Authentication authentication = authenticationManager.authenticate(token);

      final Object principal = authentication.getPrincipal();

      if (!(principal instanceof UserPrincipal userAuthenticated))
         throw new AuthenticationAttemptFailureException("Cannot resolve the authenticated subject.");

      return authFactory.createSigInResponseFrom(userAuthenticated);
   }

   @Override
   public CookieResponse refresh(final String token) {
      blacklistService.blacklistToken(token);
      return authFactory.createRefreshCookieResponseFrom(token);
   }
}
