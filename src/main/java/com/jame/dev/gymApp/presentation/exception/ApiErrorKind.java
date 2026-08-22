package com.jame.dev.gymApp.presentation.exception;

import com.jame.dev.gymApp.application.model.ErrorCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorKind {

   RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND),
   CLAIM_EXTRACTION_CONFLICT(HttpStatus.CONFLICT, ErrorCodes.EXTRACTION),
   AUTHENTICATION_FAILURE(HttpStatus.UNAUTHORIZED, ErrorCodes.AUTHENTICATION),
   SAVE_CONFLICT(HttpStatus.CONFLICT, ErrorCodes.SAVE),
   ACCESS_DENIED(HttpStatus.FORBIDDEN, ErrorCodes.ACCESS_DENIED),
   VERIFICATION_REQUIRED(HttpStatus.FORBIDDEN, ErrorCodes.VERIFICATION),
   ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, ErrorCodes.ARGUMENT),
   ACCESS_EXPIRED(HttpStatus.FORBIDDEN, ErrorCodes.ACCESS),
   UPDATE_CONFLICT(HttpStatus.CONFLICT, ErrorCodes.UPDATE),
   NO_ACCESS(HttpStatus.UNAUTHORIZED, ErrorCodes.NO_ACCESS),
   INACTIVE_STATE_CONFLICT(HttpStatus.CONFLICT, ErrorCodes.VALIDATION),
   VALIDATION_FAILURE(HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION),
   TYPE_MISMATCH(HttpStatus.BAD_REQUEST, ErrorCodes.TYPE),
   CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, ErrorCodes.CONSTRAINT),
   EXTERNAL_AUTH_UNSUPPORTED(HttpStatus.UNAUTHORIZED, ErrorCodes.SAVE),
   INTERNAL_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL),
   VERIFICATION_ATTEMPTS_EXHAUSTED(HttpStatus.BAD_REQUEST, ErrorCodes.UPDATE),
   VERIFICATION_WINDOW_EXPIRED(HttpStatus.BAD_REQUEST, ErrorCodes.ACCESS),
   TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, ErrorCodes.ACCESS),
   ACCOUNT_LOCKED(HttpStatus.LOCKED, ErrorCodes.ACCESS_DENIED),
   UNTRUSTED_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCodes.ACCESS_DENIED),
   NOTIFICATION_UNAVAILABLE(HttpStatus.UNAUTHORIZED, ErrorCodes.UNSUPPORTED),
   OPERATION_UNSUPPORTED(HttpStatus.CONFLICT, ErrorCodes.UNSUPPORTED),
   DATA_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCodes.ARGUMENT);

   private final HttpStatus status;
   private final ErrorCodes code;
}
