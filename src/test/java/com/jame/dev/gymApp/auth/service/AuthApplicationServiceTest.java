package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.features.auth.application.service.AuthApplicationService;
import com.jame.dev.gymApp.features.auth.application.support.factory.AuthResponsesFactory;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationAttemptFailureException;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.infrastructure.cache.BlacklistService;
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

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthApplicationServiceTest {

   @Mock
   UserMutationRepository userRepository;
   @Mock
   UserFactory userFactory;
   @Mock
   AuthenticationManager authenticationManager;
   @Mock
   BlacklistService blacklistService;
   @Mock
   AuthResponsesFactory authFactory;

   @InjectMocks
   private AuthApplicationService service;

   @Nested
   @DisplayName("Tests for signUp method")
   class SignUpTests {

      @Test
      @DisplayName("Should successfully sign up user")
      void signUpShouldSucceed() {
         var input = new RegisterRequest(
                 "test",
                 "email@test.com",
                 "pass");
         var userEntity = mock(UserEntity.class);
         var optionalUE = Optional.ofNullable(userEntity);

         given(userFactory.fromRegister(any(RegisterRequest.class)))
            .willReturn(userEntity);
         given(userRepository.save(any(UserEntity.class)))
            .willReturn(userEntity);

         final boolean succeed = assertDoesNotThrow(() -> service.signUp(input));

         assertTrue(succeed, "SignUp should succeed.");

         verify(userFactory).fromRegister(any(RegisterRequest.class));
         verify(userRepository).save(any(UserEntity.class));
         verifyNoMoreInteractions(userRepository, userFactory);
         verifyNoInteractions(authenticationManager, blacklistService, authFactory);
      }
   }

   @Test
   @DisplayName("Should throws NoSuchElementException when UserEntity is not present")
   void signUp_UserEntity_notPresent() {
      var input = new RegisterRequest(
         "test",
         "email@test.com",
         "pass");

      given(userFactory.fromRegister(any(RegisterRequest.class)))
         .willReturn(null);
      given(userRepository.save(any()))
         .willReturn(null);

      assertThrowsExactly(NoSuchElementException.class, () -> service.signUp(input));

      verify(userFactory).fromRegister(any(RegisterRequest.class));
      verify(userRepository).save(any());
      verifyNoMoreInteractions(userRepository, userFactory);
      verifyNoInteractions(authenticationManager, blacklistService, authFactory);
   }

   @Nested
   @DisplayName("Tests for signIn method")
   class SignInTests {

      @Test
      @DisplayName("Should successfully sign in user")
      void signInShouldReturnSignInOkDtoOnSuccess() {
         SignInRequest signInRequest = new SignInRequest("email@test.com", "pass");
         Authentication authentication = mock(Authentication.class);
         UserPrincipal user = mock(UserPrincipal.class);
         SignInResponse expectedResponse = mock(SignInResponse.class);

         given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                 .willReturn(authentication);
         given(authentication.getPrincipal()).willReturn(user);
         given(authFactory.createSigInResponseFrom(user)).willReturn(expectedResponse);

         SignInResponse result = service.signIn(signInRequest);

         assertNotNull(result);
         assertEquals(expectedResponse, result);
         verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
         verify(authFactory).createSigInResponseFrom(user);
      }

      @Test
      @DisplayName("Should throw AuthenticationAttemptFailureException when principal is null")
      void signInShouldThrowExceptionWhenPrincipalIsNull() {
         SignInRequest signInRequest = new SignInRequest("email@test.com", "pass");
         Authentication authentication = mock(Authentication.class);

         given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                 .willReturn(authentication);
         given(authentication.getPrincipal()).willReturn(null);

         assertThrowsExactly(AuthenticationAttemptFailureException.class, () -> service.signIn(signInRequest));

         verifyNoInteractions(authFactory);
      }
   }

   @Nested
   @DisplayName("Tests for refresh method")
   class RefreshTests {

      @Test
      @DisplayName("Should successfully blacklist token and return CookieResponse")
      void refreshShouldBlacklistTokenAndReturnResponse() {
         String token = "valid-token";
         CookieResponse expectedResponse = mock(CookieResponse.class);

         given(authFactory.createRefreshCookieResponseFrom(token)).willReturn(expectedResponse);

         CookieResponse result = service.refresh(token);

         assertNotNull(result);
         verify(blacklistService).blacklistToken(token);
         verify(authFactory).createRefreshCookieResponseFrom(token);
         verifyNoMoreInteractions(blacklistService, authFactory);
      }
   }
}
