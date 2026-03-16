package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.aspects.annotations.CheckSignIn;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;
import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.exception.AuthProviderNotAllowedException;
import com.jame.dev.gymApp.exception.AuthenticationAttemptFailureException;
import com.jame.dev.gymApp.factories.AuthResponsesFactory;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.auth.SignInOkDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImplementation implements AuthService {
   private final UserService userService;
   private final AuthenticationManager authenticationManager;
   private final BlacklistService blacklistService;
   private final AuthResponsesFactory authFactory;

   @Override
   public void signUp(final UserDtoInput dto) {
      if (dto.authProvider() != AuthProvider.LOCAL) {
         throw new AuthProviderNotAllowedException("Non LOCAL provider present");
      }
      final UserDtoOutput userDtoOutput = userService.save(wrapDto(dto));
      Objects.requireNonNull(userDtoOutput, "Dto object is null.");
   }

   @Override
   @CheckSignIn
   public SignInOkDto signIn(SignInDto dto) {
      final UsernamePasswordAuthenticationToken token =
              new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

      final Authentication authentication = authenticationManager.authenticate(token);
      log.info("[Auth-Service]: Auth done.");

      final User userAuthenticated = Optional.ofNullable((User) authentication.getPrincipal())
              .orElseThrow(() -> new AuthenticationAttemptFailureException("Can't authenticate User."));

      return authFactory.createSignInOkDtoFrom(userAuthenticated);
   }

   @Override
   public CookieResponseDto refresh(@NotEmptyNull final String token) {
      blacklistService.blacklistToken(token);
      return authFactory.createRefreshCookieResponseFrom(token);
   }

   private UserDtoInput wrapDto(final UserDtoInput input) {
      return UserDtoInput.builder()
              .name(input.name())
              .email(input.email())
              .password(input.password())
              .authProvider(AuthProvider.LOCAL)
              .roles(Set.of(Role.USER))
              .build();
   }
}
