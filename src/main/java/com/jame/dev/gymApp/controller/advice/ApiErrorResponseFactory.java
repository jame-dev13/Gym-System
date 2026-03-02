package com.jame.dev.gymApp.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiErrorResponseFactory {
   private final ObjectMapper mapper;

   public ResponseEntity<ApiErrorResponse> buildResponse(final InputError inputError) {
      final ApiErrorResponse errorResponse = buildError(inputError);
      return ResponseEntity
              .status(errorResponse.status())
              .body(errorResponse);
   }

   public String jsonErrorResponse (final InputError inputError) {
      try{
         return mapper.writeValueAsString(buildError(inputError));
      }catch(JsonProcessingException e){
         throw new RuntimeException("Error mapping error.", e);
      }
   }

   private ApiErrorResponse buildError(final InputError inputError) {
      final Throwable th = inputError.ex();
      final var request = inputError.request();
      final String msg = switch (th) {
         case BindException ex -> extractMessage(ex);
         case ConstraintViolationException ex -> extractMessage(ex);
         default -> th.getMessage();
      };

      return ApiErrorResponse.builder()
              .timestamp(OffsetDateTime.now())
              .status(inputError.httpStatusCode().value())
              .error(inputError.httpStatusCode().getReasonPhrase())
              .message(msg)
              .path(request.getRequestURI())
              .code(inputError.errorCode().getCode())
              .build();
   }

   private String extractMessage(final BindException bindException) {
      return bindException.getBindingResult()
              .getFieldErrors()
              .stream()
              .map(FieldError::getDefaultMessage)
              .collect(Collectors.joining(" "));
   }

   private String extractMessage(final ConstraintViolationException constraintViolationException) {
      return constraintViolationException.getConstraintViolations()
              .stream()
              .map(ConstraintViolation::getMessage)
              .collect(Collectors.joining(" "));
   }


}