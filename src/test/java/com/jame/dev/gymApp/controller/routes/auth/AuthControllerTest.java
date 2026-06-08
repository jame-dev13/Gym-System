package com.jame.dev.gymApp.controller.routes.auth;


import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.api.AuthController;
import com.jame.dev.gymApp.features.auth.domain.exception.*;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotFoundException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.auth.application.contract.AuthService;
import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import config.TestConfig;
import config.TestValidationConfig;
import com.jame.dev.gymApp.features.auth.application.support.helper.CookieHelper;
import com.jame.dev.gymApp.presentation.exception.ApiErrorResponseFactory;
import com.jame.dev.gymApp.presentation.exception.GlobalExceptionHandler;
import data.SignInTestData;
import data.SignUpTestData;
import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
   controllers = AuthController.class,
   excludeFilters = {
      @ComponentScan.Filter(
         classes = CustomAuthorizationFilter.class,
         type = FilterType.ASSIGNABLE_TYPE)
   })
@AutoConfigureMockMvc(addFilters = false)
@Import({
   GlobalExceptionHandler.class,
   TestConfig.class,
   TestValidationConfig.class
})
@ImportAutoConfiguration({ValidationAutoConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthControllerTest {
   @Autowired
   MockMvc mockMvc;

   @Autowired
   AuthController authController;

   @Autowired
   ApiErrorResponseFactory responseFactory;

   @MockitoBean
   AuthService authService;

   @MockitoBean
   CookieHelper cookieHelper;

   @Nested
   @DisplayName("Tests for signUp method")
   class SignUpTests {

      @Test
      @DisplayName("[201 Created] Should successfully sign up a new user")
      void signUpSuccess() throws Exception {
         willDoNothing().given(authService).signUp(any(RegisterRequest.class));

         mockMvc.perform(post("/auth/signUp")
               .contentType(MediaType.APPLICATION_JSON)
               .content(SignUpTestData.VALID_USER_JSON))
            .andExpect(status().isCreated());

         verify(authService).signUp(any(RegisterRequest.class));
         verifyNoMoreInteractions(authService);
         verifyNoInteractions(cookieHelper);
      }

      @ParameterizedTest
      @MethodSource("serviceFailures")
      @DisplayName("[4xx] Should handle failures during sign up")
      void signUpFailuresService(Class<? extends Throwable> exception, int statusCode, String body) throws Exception {
         willThrow(exception).given(authService).signUp(any(RegisterRequest.class));

         performSignUp(body)
            .andExpect(status().is(statusCode))
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(statusCode));

         verify(authService).signUp(any(RegisterRequest.class));
         verifyNoMoreInteractions(authService);
         verifyNoInteractions(cookieHelper);
      }

      @ParameterizedTest
      @MethodSource("controllerFailures")
      @DisplayName("[400] Should handle failures during sign up")
      void signUpFailuresController(Class<? extends Throwable> exception, int statusCode, String body) throws Exception {
         performSignUp(body)
            .andExpect(status().is(statusCode))
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(statusCode));

         verifyNoInteractions(authService, cookieHelper);
      }

      private ResultActions performSignUp(String body) throws Exception {
         return mockMvc.perform(post("/auth/signUp")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body));
      }

      private static Stream<Arguments> serviceFailures() {
         return Stream.of(
            Arguments.of(AuthProviderNotAllowedException.class, 400, SignUpTestData.VALID_USER_JSON),
            Arguments.of(ConstraintViolationException.class, 400, SignUpTestData.ADMIN_USER_JSON),
            Arguments.of(NoActiveException.class, 409, SignUpTestData.VALID_USER_JSON),
            Arguments.of(AlreadyExistsException.class, 409, SignUpTestData.VALID_USER_JSON)
         );
      }

      private static Stream<Arguments> controllerFailures() {
         return Stream.of(
            Arguments.of(MethodArgumentTypeMismatchException.class, 400, SignUpTestData.INVALID_JSON),
            Arguments.of(ConstraintViolationException.class, 400, """
               {"name": "", "email": "test@test.com", "password": "pass123"}""")
         );
      }
   }

   @Nested
   @DisplayName("Tests for signIn method")
   class SignInTests {

      @Test
      @DisplayName("[200 Ok] Should successfully sign in and return cookies")
      void signInSuccess() throws Exception {
         SignInResponse mockResponse = mock(SignInResponse.class);

         given(authService.signIn(any(SignInRequest.class)))
            .willReturn(mockResponse);
         given(cookieHelper.createAccessTokenCookie(any()))
            .willReturn(ResponseCookie.from("access", "val").build());
         given(cookieHelper.createRefreshTokenCookie(any()))
            .willReturn(ResponseCookie.from("refresh", "val").build());

         mockMvc.perform(post("/auth/signIn")
               .contentType(MediaType.APPLICATION_JSON)
               .content("""
                  {"email": "test@example.com", "password": "password123"}"""))
            .andExpect(status().isOk())
            .andExpect(header().exists("Set-Cookie"));

         verify(authService).signIn(any(SignInRequest.class));
         verify(cookieHelper).createAccessTokenCookie(any());
         verify(cookieHelper).createRefreshTokenCookie(any());
         verifyNoMoreInteractions(authService, cookieHelper);
      }

      @ParameterizedTest
      @MethodSource("failures")
      @DisplayName("[4xx] Should handle failures during signIn service runs.")
      void signInFailures(Class<? extends Throwable> exception, int statusCode) throws Exception {
         given(authService.signIn(any(SignInRequest.class))).willThrow(exception);

         mockMvc.perform(post("/auth/signIn")
               .contentType(MediaType.APPLICATION_JSON)
               .content(SignInTestData.JSON_VALID))
            .andExpect(status().is(statusCode))
            .andExpect(jsonPath("$.status").value(statusCode));

         verify(authService).signIn(any(SignInRequest.class));
         verifyNoMoreInteractions(authService);
         verifyNoInteractions(cookieHelper);
      }

      @ParameterizedTest
      @MethodSource("controllerFailures")
      @DisplayName("[400 Bad Request] Should handle failures during signIn controller checks.")
      void signInControllerFailures(Class<? extends Throwable> ignored, int statusCode, String body) throws Exception {
         mockMvc.perform(post("/auth/signIn")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
            .andExpect(status().is(statusCode))
            .andExpect(jsonPath("$.status").value(statusCode));

         verifyNoInteractions(authService, cookieHelper);
      }

      private static Stream<Arguments> failures() {
         return Stream.of(
            Arguments.of(UserNotFoundException.class, 404),
            Arguments.of(NoActiveException.class, 409),
            Arguments.of(AuthenticationAttemptFailureException.class, 401)
         );
      }

      private static Stream<Arguments> controllerFailures() {
         return Stream.of(
            Arguments.of(ConstraintViolationException.class, 400, SignInTestData.JSON_INVALID),
            Arguments.of(MethodArgumentTypeMismatchException.class, 400,
               """
                  {}
                  """)
         );
      }
   }

   @Nested
   @DisplayName("Tests for refresh method")
   class RefreshTests {

      @Test
      @DisplayName("[200 Ok] Should successfully refresh tokens using cookie")
      void refresh_Success() throws Exception {
         CookieResponse mockCookies = new CookieResponse("new-access", "new-refresh");

         given(authService.refresh(any())).willReturn(mockCookies);
         given(cookieHelper.createAccessTokenCookie(any()))
            .willReturn(ResponseCookie.from("access", "val").build());
         given(cookieHelper.createRefreshTokenCookie(any()))
            .willReturn(ResponseCookie.from("refresh", "val").build());

         mockMvc.perform(post("/auth/refresh")
               .cookie(new Cookie("refresh", "valid-token")))
            .andExpect(status().isOk())
            .andExpect(header().exists("Set-Cookie"));

         verify(authService).refresh(any());
         verify(cookieHelper).createAccessTokenCookie(any());
         verify(cookieHelper).createRefreshTokenCookie(any());
         verifyNoMoreInteractions(authService, cookieHelper);
      }

      @ParameterizedTest
      @MethodSource("failures")
      @DisplayName("[409 Conflict] Should handle failures during token refresh service call.")
      void refreshFailures(Class<? extends Throwable> exception, int statusCode) throws Exception {
         given(authService.refresh(any())).willThrow(exception);

         mockMvc.perform(post("/auth/refresh")
               .cookie(new jakarta.servlet.http.Cookie("refresh", "invalid-token")))
            .andExpect(status().is(statusCode))
            .andExpect(jsonPath("$.status").value(statusCode));

         verify(authService).refresh(any());
         verifyNoMoreInteractions(authService);
         verifyNoInteractions(cookieHelper);
      }

      @Test
      @DisplayName("""
         [400 Bad Request]: Should handle Bad Request due to cookie value empty or null
         and never touches the services.
         """)
      void refreshControllerFailures() throws Exception  {
         mockMvc.perform(post("/auth/refresh")
               .cookie(new jakarta.servlet.http.Cookie("refresh", "")))
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.status").value(400));

         verifyNoInteractions(authService, cookieHelper);
      }

      private static Stream<Arguments> failures() {
         return Stream.of(
            Arguments.of(ExtractClaimException.class, 409),
            Arguments.of(TokenAlreadyBlacklistedException.class, 409)
         );
      }
   }
}
