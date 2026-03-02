package com.jame.dev.gymApp.auth.handlers;

import com.jame.dev.gymApp.auth.service.LogoutService;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.InputError;
import com.jame.dev.gymApp.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.shared.enums.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

   private final LogoutService logoutService;
   private final ApiErrorResponseFactory responseFactory;

   @Override
   public void logout(HttpServletRequest request,
                      HttpServletResponse response,
                      @Nullable Authentication authentication) {
      try {
         logoutService.logout(request, response);
      } catch (TokenAlreadyBlacklistedException e) {
         final String body = responseFactory
                 .jsonErrorResponse(
                         new InputError(e, request, HttpStatus.BAD_REQUEST, ErrorCodes.LOGOUT));
         writeResponse(response, body);
      }
   }

   private void writeResponse(HttpServletResponse response, String json) {
      try {
         final PrintWriter writer = response.getWriter();
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("application/json");
         writer.write(json);
         writer.flush();
      } catch (IOException e) {
         log.error("Cannot write the response: {}", e.getMessage());
      }
   }
}
