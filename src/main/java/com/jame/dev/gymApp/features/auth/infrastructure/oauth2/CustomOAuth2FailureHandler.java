package com.jame.dev.gymApp.features.auth.infrastructure.oauth2;

import com.jame.dev.gymApp.presentation.exception.ApiErrorResponseFactory;
import com.jame.dev.gymApp.presentation.exception.InputError;
import com.jame.dev.gymApp.application.model.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

   private final ApiErrorResponseFactory responseFactory;

   @Override
   public void onAuthenticationFailure(@NonNull HttpServletRequest request,
                                       HttpServletResponse response,
                                       @NonNull AuthenticationException exception) throws IOException {
      final InputError inputError = new InputError(
         exception, request,
         HttpStatus.UNAUTHORIZED,
         ErrorCodes.AUTHENTICATION
      );
      final String payload = responseFactory
         .jsonErrorResponse(inputError);
      final PrintWriter writer = response.getWriter();
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      writer.write(payload);
      writer.flush();
   }
}
