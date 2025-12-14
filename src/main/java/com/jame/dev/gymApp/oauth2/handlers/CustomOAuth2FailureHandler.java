package com.jame.dev.gymApp.oauth2.handlers;

import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
   public void onAuthenticationFailure(HttpServletRequest request,
                                       HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {
      final String payload = responseFactory
              .jsonErrorResponse(exception, request, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILURE");
      final PrintWriter writer =  response.getWriter();
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      writer.write(payload);
      writer.flush();
   }
}
