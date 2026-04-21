package com.jame.dev.gymApp.auth.handlers;

import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.InputError;
import com.jame.dev.gymApp.shared.enums.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
   private final ApiErrorResponseFactory responseFactory;

   @Override
   public void handle(
      @NonNull HttpServletRequest request,
      HttpServletResponse response,
      @NonNull AccessDeniedException accessDeniedException)
      throws IOException {
      final InputError inputError = new InputError(
         accessDeniedException,
         request,
         HttpStatus.FORBIDDEN,
         ErrorCodes.ACCESS_DENIED
      );
      final String payload = responseFactory
         .jsonErrorResponse(inputError);
      final PrintWriter writer = response.getWriter();
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      writer.write(payload);
      writer.flush();
   }
}
