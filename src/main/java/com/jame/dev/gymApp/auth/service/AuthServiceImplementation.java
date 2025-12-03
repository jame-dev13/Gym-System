package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationService;
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
      final UserEntity user = userService.save(dto);
      if (user == null) {
         throw new CantSaveUserException("Operation failed.");
      }
      final VerificationEntity verification = verificationService.save(user);
      //Send Email confirmation with Email Service
      if (verification == null) {
         throw new CantSaveVerifcationEntityException("Can't save the verification.");
      }
      final String recipient = user.getEmail();
      EmailDetails emailDetails = EmailDetails.builder()
              .recipient(recipient)
              .subject("Verification code")
              .msgBody(emailService.html(recipient, verification.getId()))
              .build();
      //send email
      CompletableFuture<Boolean> emailSent = emailService.sendSimpleEmail(emailDetails);
      log.warn((emailSent.get()) ? "email successfully sent." : "cant sent the email.");
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
