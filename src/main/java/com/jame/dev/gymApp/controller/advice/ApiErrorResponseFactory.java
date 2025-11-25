package com.jame.dev.gymApp.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class ApiErrorResponseFactory {
   public ApiErrorResponse buildErrorResponse(final Throwable th,
                                                     final HttpServletRequest request,
                                                     final HttpStatus status,
                                                     final String code){
      return ApiErrorResponse.builder()
              .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
              .status(status.value())
              .error(status.getReasonPhrase())
              .message(th.getMessage())
              .path(request.getRequestURI())
              .code(code)
              .build();
   }
}