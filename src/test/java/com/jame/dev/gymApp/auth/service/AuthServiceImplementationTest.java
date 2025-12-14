package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplementationTest {

   @Mock
   private UserService userService;
   @Mock
   private JwtService jwtService;
   @Mock
   private CookieHelper cookieHelper;
   @Mock
   private VerificationService verificationService;
   @Mock
   private AuthenticationManager authenticationManager;
   @Mock
   private BlacklistService blacklistService;
   @Mock
   private EmailService emailService;

   @InjectMocks
   private AuthServiceImplementation service;

   private final UserEntity user = UserEntity.builder()
           .id(1L)
           .name("userTest")
           .email("userTest@mail.com")
           .password("user123")
           .provider(AuthProvider.LOCAL)
           .roles(Set.of(new RoleEntity(1, Role.USER)))
           .active(true)
           .build();
   private final VerificationEntity verification = VerificationEntity.builder()
           .id("123ABC")
           .user(user)
           .expiration(Instant.now().plus(10, ChronoUnit.MINUTES))
           .verified(false)
           .build();
   @Captor
   private ArgumentCaptor<UserDtoInput> dtoCaptor;
   @Captor
   private ArgumentCaptor<UserEntity> entityCaptor;
   @Captor
   private ArgumentCaptor<String> emailCaptor;
   @Captor
   private ArgumentCaptor<String> codeCaptor;
   @Captor
   ArgumentCaptor<String> cookieAccessCaptor;
   @Captor
   ArgumentCaptor<String> cookieRefreshCaptor;


   @Test
   @DisplayName("Sign-Up: Successful signUp and email send.")
   void signUp() throws ExecutionException, InterruptedException {
      final UserDtoInput dto = UserDtoInput.builder()
              .name("dto")
              .email("dto@mail.com")
              .password("133453")
              .roles(Set.of(Role.USER))
              .authProvider(AuthProvider.LOCAL)
              .build();
      final String html = "<p>Hola</p>";
      when(userService.save(any(UserDtoInput.class))).thenReturn(user);
      when(verificationService.save(any(UserEntity.class))).thenReturn(verification);
      when(emailService.html(anyString(), anyString()))
              .thenReturn(html);
      when(emailService.sendSimpleEmail(any(EmailDetails.class)))
              .thenReturn(CompletableFuture.completedFuture(true));

      assertDoesNotThrow(() -> service.signUp(dto), "Should not throw any Exception.");

      verify(userService, times(1)).save(dtoCaptor.capture());
      verify(verificationService, times(1)).save(entityCaptor.capture());
      verify(emailService, atLeastOnce()).html(emailCaptor.capture(), codeCaptor.capture());
      verify(emailService, atLeastOnce()).sendSimpleEmail(any(EmailDetails.class));
      verifyNoMoreInteractions(userService, verificationService, emailService);
   }

   @Test
   @DisplayName("Sign-In: Successful authentication and cookies with token generation.")
   void signIn() {
      final SignInDto dto = SignInDto.builder()
              .email(this.user.getEmail())
              .password(this.user.getPassword())
              .build();
      final User userAuthMock = new User(
              dto.email(),
              dto.password(),
              Collections.emptyList()
      );

      final Authentication authMock = mock(Authentication.class);
      when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(this.user));
      when(verificationService.isVerified(anyString())).thenReturn(true);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
      when(authMock.getPrincipal()).thenReturn(userAuthMock);

      when(jwtService.generateAccessToken(dto.email())).thenAnswer(inv -> inv.getArgument(0));
      when(jwtService.generateRefreshToken(dto.email())).thenAnswer(inv -> inv.getArgument(0));

      final ResponseCookie accessCookieMock = mock(ResponseCookie.class);
      final ResponseCookie refreshCookieMock = mock(ResponseCookie.class);

      when(cookieHelper.createAccessTokenCookie(any(String.class))).thenReturn(accessCookieMock);
      when(cookieHelper.createRefreshTokenCookie(any(String.class))).thenReturn(refreshCookieMock);

      final CookieResponseDto response = service.signIn(dto);

      verify(userService, atLeastOnce()).getUserByEmail(anyString());
      verify(authenticationManager, atLeastOnce()).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(authMock, atLeastOnce()).getPrincipal();
      verify(jwtService, atLeastOnce()).generateAccessToken(dto.email());
      verify(jwtService, atLeastOnce()).generateRefreshToken(dto.email());
      verify(cookieHelper, atLeastOnce()).createAccessTokenCookie(cookieAccessCaptor.capture());
      verify(cookieHelper, atLeastOnce()).createRefreshTokenCookie(cookieRefreshCaptor.capture());
      verifyNoMoreInteractions(userService, authenticationManager, jwtService, cookieHelper);

      assertNotNull(response, "Should not be null.");
      assertEquals(response.access(), accessCookieMock, "Should be the same object.");
      assertSame(response.access().getValue(), accessCookieMock.getValue(), "Should have the save value.");
      assertEquals(response.refresh(), refreshCookieMock, "Should be the same object.");
      assertSame(response.refresh().getValue(), refreshCookieMock.getValue(), "Should have the save value.");
   }

   @Test
   @DisplayName("Refresh token: refresh tokens and blacklist the given refresh token.")
   void refresh() {
      final String subject = "subject";
      final ResponseCookie accessCookieMock = mock(ResponseCookie.class);
      final ResponseCookie refreshCookieMock = mock(ResponseCookie.class);
      final String refreshValue = refreshCookieMock.getValue();
      when(jwtService.extractSubject(refreshValue))
              .thenReturn(Optional.of(subject));
      when(jwtService.isValid(refreshValue, subject))
              .thenReturn(true);

      when(jwtService.generateAccessToken(subject)).thenAnswer(inv -> inv.getArgument(0));
      when(jwtService.generateRefreshToken(subject)).thenAnswer(inv -> inv.getArgument(0));

      when(cookieHelper.createAccessTokenCookie(any(String.class))).thenReturn(accessCookieMock);
      when(cookieHelper.createRefreshTokenCookie(any(String.class))).thenReturn(refreshCookieMock);

      final CookieResponseDto response = service.refresh(refreshValue);

      verify(blacklistService, atLeastOnce()).blacklistToken(refreshValue);
      verify(jwtService).extractSubject(refreshValue);
      verify(jwtService).isValid(refreshValue, subject);
      verify(jwtService).generateAccessToken(subject);
      verify(jwtService).generateRefreshToken(subject);
      verify(cookieHelper).createAccessTokenCookie(cookieAccessCaptor.capture());
      verify(cookieHelper).createRefreshTokenCookie(cookieRefreshCaptor.capture());

      assertEquals(subject, cookieAccessCaptor.getValue(),
              "Should be the same subject.");
      assertEquals(subject, cookieRefreshCaptor.getValue(),
              "Should be the same subject.");
      assertNotNull(response, "Should not be null.");
      assertSame(accessCookieMock, response.access(),
              "Should be the defined mock.");
      assertSame(refreshCookieMock, response.refresh(),
              "Should be the defined mock.");
   }

   @Test
   @DisplayName("Should verify the account.")
   void verification() {
      final VerificationDto dto = VerificationDto.builder()
              .timestamp(OffsetDateTime.now())
              .email("mail@mail.com")
              .verified(true)
              .msg("Verification succeed")
              .build();
      when(verificationService.verify(anyString(), anyString()))
              .thenReturn(dto);
      final Optional<VerificationDto> optVerification = service.verify(dto.email(), "ABD123");

      verify(verificationService).verify(emailCaptor.capture(), anyString());
      verifyNoMoreInteractions(verificationService);


      assertTrue(optVerification.isPresent(), "Optional value should be present.");
   }
}
