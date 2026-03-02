package com.jame.dev.gymApp.auth.handlers;

import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.InputError;
import com.jame.dev.gymApp.shared.enums.ErrorCodes;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
   private final ApiErrorResponseFactory responseFactory;

   @Override
   public void commence(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull AuthenticationException authException)
           throws IOException, ServletException {
      final String payload = responseFactory
              .jsonErrorResponse(new InputError(authException, request, HttpStatus.UNAUTHORIZED, ErrorCodes.NO_ACCESS));
      final PrintWriter writer = response.getWriter();
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      writer.write(payload);
      writer.flush();
   }
}
