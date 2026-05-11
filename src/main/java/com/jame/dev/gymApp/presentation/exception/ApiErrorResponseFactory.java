package com.jame.dev.gymApp.presentation.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;

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
      final String msg = extractMessageFrom(inputError.ex());
      final var request = inputError.request();
      return ApiErrorResponse.builder()
              .timestamp(OffsetDateTime.now())
              .status(inputError.httpStatusCode().value())
              .error(inputError.httpStatusCode().getReasonPhrase())
              .message(msg)
              .path(request.getRequestURI())
              .code(inputError.errorCode().getCode())
              .build();
   }

   private String extractMessageFrom(Throwable th) {
      return switch (th) {
         case BindException ex -> ErrorResponseUtils.extractMessage(ex);
         case ConstraintViolationException ex -> ErrorResponseUtils.extractMessage(ex);
         case MethodArgumentTypeMismatchException ignored -> "Type not allowed.";
         case HttpMessageNotReadableException ignored -> "Payload malformed.";
         default -> th.getMessage();
      };
   }
}