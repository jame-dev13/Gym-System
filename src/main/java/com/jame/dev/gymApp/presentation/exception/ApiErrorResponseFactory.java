package com.jame.dev.gymApp.presentation.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jame.dev.gymApp.application.model.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

   public ResponseEntity<ApiErrorResponse> of(final ApiErrorKind kind,
                                              final Throwable th,
                                              final HttpServletRequest request) {
      return respond(buildError(th, request, kind.getStatus(), kind.getCode()));
   }

   /**
    * @deprecated use {@link #of(ApiErrorKind, Throwable, HttpServletRequest)} instead.
    */
   @Deprecated
   public ResponseEntity<ApiErrorResponse> buildResponse(final InputError inputError) {
      return respond(buildError(
              inputError.ex(),
              inputError.request(),
              inputError.httpStatusCode(),
              inputError.errorCode()
      ));
   }

   /**
    * @deprecated use {@link #of(ApiErrorKind, Throwable, HttpServletRequest)} instead.
    */
   @Deprecated
   public String jsonErrorResponse(final InputError inputError) {
      try{
         return mapper.writeValueAsString(buildError(
                 inputError.ex(),
                 inputError.request(),
                 inputError.httpStatusCode(),
                 inputError.errorCode()
         ));
      }catch(JsonProcessingException e){
         throw new RuntimeException("Error mapping error.", e);
      }
   }

   private ApiErrorResponse buildError(final Throwable th,
                                       final HttpServletRequest request,
                                       final HttpStatus status,
                                       final ErrorCodes errorCode) {
      final String msg = extractMessageFrom(th);
      return ApiErrorResponse.builder()
              .timestamp(OffsetDateTime.now())
              .status(status.value())
              .error(status.getReasonPhrase())
              .message(msg)
              .path(request.getRequestURI())
              .code(errorCode.getCode())
              .build();
   }

   private ResponseEntity<ApiErrorResponse> respond(final ApiErrorResponse errorResponse) {
      return ResponseEntity
              .status(errorResponse.status())
              .body(errorResponse);
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
