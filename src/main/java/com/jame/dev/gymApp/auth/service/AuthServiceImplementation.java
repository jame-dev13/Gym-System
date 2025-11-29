package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AuthenticationAttemptFailureException;
import com.jame.dev.gymApp.exception.CantSaveUserException;
import com.jame.dev.gymApp.exception.InvalidJwtException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.service.in.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {
   private final UserService userService;
   private final JwtService jwtService;
   private final CookieHelper cookieHelper;
   private final AuthenticationManager authenticationManager;
   private final BlacklistService blacklistService;

   @Override
   public void signUp(UserDtoInput dto) {
      final UserEntity user = userService.save(dto);
      if (Objects.isNull(user)) {
         throw new CantSaveUserException("Operation failed.");
      }
      //Send Email confirmation with Email Service
   }

   @Override
   public CookieResponseDto signIn(SignInDto dto) {
      final UsernamePasswordAuthenticationToken token =
              new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
      final Authentication authentication = authenticationManager.authenticate(token);

      final User userAuthenticated = Optional.ofNullable((User) authentication.getPrincipal())
              .orElseThrow(() -> new AuthenticationAttemptFailureException("Can't authenticate User."));
      final String username = userAuthenticated.getUsername();

      return handleCookieResponse(username);
   }

   @Override
   public CookieResponseDto refresh(String token) {
      blacklistService.blacklistToken(token);
      final String subject = jwtService.extractSubject(token)
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      final boolean isValid = jwtService.isValid(token, subject);
      if (!isValid) {
         throw new InvalidJwtException("Not valid Jwt.");
      }
      return handleCookieResponse(subject);
   }

   private CookieResponseDto handleCookieResponse(final String subject) {
      final String accessToken = jwtService.generateAccessToken(subject);
      final String refreshToken = jwtService.generateRefreshToken(subject);

      final ResponseCookie access = cookieHelper.createAccessTokenCookie(accessToken);
      final ResponseCookie refresh = cookieHelper.createRefreshTokenCookie(refreshToken);
      return new CookieResponseDto(access, refresh);
   }
}
