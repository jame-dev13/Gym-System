package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.ExpirationWindowDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImplementation implements AuthService {

   private final UserService userService;
   private final JwtService jwtService;
   private final CookieHelper cookieHelper;
   private final AuthenticationManager authenticationManager;
   private final BlacklistService blacklistService;
   private final VerificationService verificationService;
   private final EmailService emailService;

   @Override
   public void signUp(UserDtoInput dto) throws ExecutionException, InterruptedException {
      if (dto.authProvider() != AuthProvider.LOCAL) {
         throw new AuthProviderNotAllowedException("Non LOCAL provider present: " + dto.authProvider());
      }
      final UserEntity user = userService.save(dto);
      if (user == null) {
         throw new CantSaveUserException("Operation failed.");
      }
      final VerificationEntity verification = verificationService.save(user);
      if (verification == null) {
         throw new CantSaveVerifcationEntityException("Can't save the verification.");
      }
      final String recipient = user.getEmail();
      final EmailDetails emailDetails = EmailDetails.builder()
              .recipient(recipient)
              .subject("Verification code")
              .msgBody(emailService.html(recipient, verification.getId()))
              .build();
      final CompletableFuture<Boolean> emailSent = emailService.sendSimpleEmail(emailDetails);
      log.warn((emailSent.get()) ? "email successfully sent." : "cant sent the email.");
   }

   @Override
   public CookieResponseDto signIn(SignInDto dto) {
      if (!isLocalProvider(dto)) {
         throw new NonLocalAuthenticationAllowedException("This should not be authenticated by the local provider.");
      }

      if (!verificationService.isVerified(dto.email())) {
         throw new UserNotVerifiedException("%s hadn't verified his account yet.".formatted(dto.email()));
      }

      final UsernamePasswordAuthenticationToken token =
              new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
      final Authentication authentication = authenticationManager.authenticate(token);
      log.info("[Auth-Service]: Auth done.");
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

   @Override
   public Optional<VerificationDto> verify(final String email, final String code) {
      return Optional.of(verificationService.verify(email, code));
   }

   @Override
   public ExpirationWindowDto setNewExpiration(@NonNull String email) {
      return verificationService.getMoreExpTime(email);
   }

   private boolean isLocalProvider(SignInDto dto) {
      final UserEntity entityUser = userService.getUserByEmail(dto.email())
              .orElseThrow(() -> new UserNotFoundException("No user with email: " + dto.email()));
      return entityUser.getProvider() == AuthProvider.LOCAL;
   }

   private CookieResponseDto handleCookieResponse(final String subject) {
      log.info("[Cookie-Response]: HIT cookie response.");
      final String accessToken = jwtService.generateAccessToken(subject);
      final String refreshToken = jwtService.generateRefreshToken(subject);

      final ResponseCookie access = cookieHelper.createAccessTokenCookie(accessToken);
      final ResponseCookie refresh = cookieHelper.createRefreshTokenCookie(refreshToken);
      return new CookieResponseDto(access, refresh);
   }
}
