package com.jame.dev.gymApp.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ApiErrorResponseFactory {
   private final ObjectMapper mapper;

   public ApiErrorResponse buildErrorResponse(final Throwable th, final HttpServletRequest request,
                                                     final HttpStatus status, final String code){
      return build(th, request, status, code);
   }

   public String jsonErrorResponse(final Throwable th, final HttpServletRequest request,
                                   final HttpStatus status, final String code) {
      final ApiErrorResponse response = build(th, request, status, code);
      try {
         return mapper.writeValueAsString(response);
      } catch (JsonProcessingException e) {
         throw new RuntimeException(e);
      }
   }

   private ApiErrorResponse build(final Throwable th, final HttpServletRequest request,
                                  final HttpStatus status, final String code){
      return ApiErrorResponse.builder()
              .timestamp(OffsetDateTime.now())
              .status(status.value())
              .error(status.getReasonPhrase())
              .message(th.getMessage())
              .path(request.getRequestURI())
              .code(code)
              .build();
   }
}