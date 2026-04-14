package com.jame.dev.gymApp.auth.service;

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
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImplementationTest {

   @Mock
   UserService userService;
   @Mock
   AuthenticationManager authenticationManager;
   @Mock
   BlacklistService blacklistService;
   @Mock
   AuthResponsesFactory authFactory;

   @InjectMocks
   private AuthServiceImplementation service;

   @Nested
   @DisplayName("Tests for signUp method")
   class SignUpTests {

      @Test
      @DisplayName("Should successfully sign up local user")
      void signUpShouldSucceedWhenProviderIsLocal() {
         UserDtoInput inputDto = new UserDtoInput(
                 "test",
                 "email@test.com", "pass",
                 AuthProvider.LOCAL, Set.of(Role.USER));
         UserDtoOutput outputDto = mock(UserDtoOutput.class);

         given(userService.save(inputDto)).willReturn(outputDto);

         assertDoesNotThrow(() -> service.signUp(inputDto));

         verify(userService).save(inputDto);
      }

      @Test
      @DisplayName("Should throw AuthProviderNotAllowedException when provider is not LOCAL")
      void signUpShouldThrowExceptionWhenProviderIsNotLocal() {
         UserDtoInput inputDto = new UserDtoInput(
                 "test",
                 "email@test.com", "pass",
                 AuthProvider.GOOGLE, Set.of(Role.USER));

         assertThrowsExactly(AuthProviderNotAllowedException.class, () -> service.signUp(inputDto));

         verifyNoInteractions(userService);
      }

      @Test
      @DisplayName("Should throw NullPointerException when saved user is null")
      void signUpShouldThrowExceptionWhenServiceReturnsNull() {
         UserDtoInput inputDto = new UserDtoInput(
                 "test",
                 "email@test.com", "pass",
                 AuthProvider.LOCAL, Set.of(Role.USER));

         given(userService.save(inputDto)).willReturn(null);

         assertThrowsExactly(NullPointerException.class, () -> service.signUp(inputDto));

         verify(userService).save(inputDto);
      }
   }

   @Nested
   @DisplayName("Tests for signIn method")
   class SignInTests {

      @Test
      @DisplayName("Should successfully sign in user")
      void signInShouldReturnSignInOkDtoOnSuccess() {
         SignInDto signInDto = new SignInDto("email@test.com", "pass");
         Authentication authentication = mock(Authentication.class);
         User user = mock(User.class);
         SignInOkDto expectedResponse = mock(SignInOkDto.class);

         given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                 .willReturn(authentication);
         given(authentication.getPrincipal()).willReturn(user);
         given(authFactory.createSignInOkDtoFrom(user)).willReturn(expectedResponse);

         SignInOkDto result = service.signIn(signInDto);

         assertNotNull(result);
         assertEquals(expectedResponse, result);
         verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
         verify(authFactory).createSignInOkDtoFrom(user);
      }

      @Test
      @DisplayName("Should throw AuthenticationAttemptFailureException when principal is null")
      void signInShouldThrowExceptionWhenPrincipalIsNull() {
         SignInDto signInDto = new SignInDto("email@test.com", "pass");
         Authentication authentication = mock(Authentication.class);

         given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                 .willReturn(authentication);
         given(authentication.getPrincipal()).willReturn(null);

         assertThrowsExactly(AuthenticationAttemptFailureException.class, () -> service.signIn(signInDto));

         verifyNoInteractions(authFactory);
      }
   }

   @Nested
   @DisplayName("Tests for refresh method")
   class RefreshTests {

      @Test
      @DisplayName("Should successfully blacklist token and return CookieResponseDto")
      void refreshShouldBlacklistTokenAndReturnResponse() {
         String token = "valid-token";
         CookieResponseDto expectedResponse = mock(CookieResponseDto.class);

         given(authFactory.createRefreshCookieResponseFrom(token)).willReturn(expectedResponse);

         CookieResponseDto result = service.refresh(token);

         assertNotNull(result);
         verify(blacklistService).blacklistToken(token);
         verify(authFactory).createRefreshCookieResponseFrom(token);
         verifyNoMoreInteractions(blacklistService, authFactory);
      }
   }
}
