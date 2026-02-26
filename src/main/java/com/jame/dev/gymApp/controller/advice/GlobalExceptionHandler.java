package com.jame.dev.gymApp.controller.advice;

import com.jame.dev.gymApp.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
   private final ApiErrorResponseFactory responseFactory;

   @ExceptionHandler(UserNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleUserNotFoundException(final UserNotFoundException ex,
                                                                                final HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(errorResponse);
   }

   @ExceptionHandler(CustomerNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleCustomerNotFoundException(final CustomerNotFoundException ex,
                                                                                    final HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND");
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(errorResponse);
   }

   @ExceptionHandler(MembershipNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleMembershipNotFoundException(final MembershipNotFoundException ex,
                                                                                      final HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "MEMBERSHIP_NOT_FOUND");
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(errorResponse);
   }

   @ExceptionHandler(PeriodNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handlePeriodNotFoundException(final PeriodNotFoundException ex,
                                                                                  final HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "PERIOD_NOT_FOUND");
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(errorResponse);
   }

   @ExceptionHandler(PricingNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handlePricingNotFoundException(final PricingNotFoundException ex,
                                                                                   final HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "PRICING_NOT_FOUND");
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(errorResponse);
   }

   @ExceptionHandler(SubscriptionNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleSubscriptionNotFoundException(final SubscriptionNotFoundException ex,
                                                                                        final HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "SUBSCRIPTION_NOT_FOUND");
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(errorResponse);
   }

   @ExceptionHandler(NoOperationException.class)
   public ResponseEntity<ApiErrorResponse> handleNoOperationException(
           NoOperationException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "UNSUPPORTED_OPERATION");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
   }

   @ExceptionHandler(ExtractClaimException.class)
   public ResponseEntity<ApiErrorResponse> handleExtractClaimException(
           ExtractClaimException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "EXTRACTION_OPERATION");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
   }

   @ExceptionHandler(AuthenticationAttemptFailureException.class)
   public ResponseEntity<ApiErrorResponse> handleAuthenticationAttemptFailureException(
           AuthenticationAttemptFailureException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.FORBIDDEN, "AUTH_OPERATION");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
   }

   @ExceptionHandler(AuthenticationNullException.class)
   public ResponseEntity<ApiErrorResponse> handleAuthenticationNullException(
           AuthenticationNullException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "NO_ACCESS");
      return ResponseEntity.status(errorResponse.status()).body(errorResponse);
   }

   @ExceptionHandler(CantSaveUserException.class)
   public ResponseEntity<ApiErrorResponse> handleCantSaveUserException(
           CantSaveUserException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "SERVICE_OPERATION");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
   }

   @ExceptionHandler(CantSaveVerifcationEntityException.class)
   public ResponseEntity<ApiErrorResponse> handleCantSaveVerificationEntityException(
           CantSaveVerifcationEntityException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "SERVICE_OPERATION");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
   }

   @ExceptionHandler(EmptyCacheObjectException.class)
   public ResponseEntity<ApiErrorResponse> handleEmptyCacheObjectException(
           EmptyCacheObjectException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "CACHE_OPERATION");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
   }

   @ExceptionHandler(IndexNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleIndexNotFoundException(
           IndexNotFoundException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "CACHE_OPERATION");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
   }

   @ExceptionHandler(InvalidJwtException.class)
   public ResponseEntity<ApiErrorResponse> handleInvalidJwtException(
           InvalidJwtException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "AUTH_OPERATION");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
   }

   @ExceptionHandler(InvalidJwtSecretException.class)
   public ResponseEntity<ApiErrorResponse> handleInvalidJwtSecretException(
           InvalidJwtSecretException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "AUTH_OPERATION");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
   }

   @ExceptionHandler(InvalidSignedJwtKeyException.class)
   public ResponseEntity<ApiErrorResponse> handleInvalidSignedJwtKeyException(
           InvalidSignedJwtKeyException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "AUTH_OPERATION");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
   }

   @ExceptionHandler(RoleNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleRoleNotFoundException(
           RoleNotFoundException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND_OPERATION");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
   }

   @ExceptionHandler(TokenAlreadyBlacklistedException.class)
   public ResponseEntity<ApiErrorResponse> handleTokenAlreadyBlacklistedException(
           TokenAlreadyBlacklistedException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.CONFLICT, "AUTH_OPERATION");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
   }

   @ExceptionHandler(VerificationTokenNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleVerificationTokenNotFoundException(
           VerificationTokenNotFoundException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "AUTH_OPERATION");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
   }

   @ExceptionHandler(UserNotVerifiedException.class)
   public ResponseEntity<ApiErrorResponse> handleUserNotVerifiedException(
           UserNotVerifiedException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "AUTH_OPERATION");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
           IllegalArgumentException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "ARGUMENT_OPERATION");
      return ResponseEntity.status(errorResponse.status()).body(errorResponse);
   }

   @ExceptionHandler(AlreadyExistsException.class)
   public ResponseEntity<ApiErrorResponse> handleAlreadyExistsException(
           AlreadyExistsException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.CONFLICT, "SAVE_OPERATION");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
   }

   @ExceptionHandler(AccessExpiredException.class)
   public ResponseEntity<ApiErrorResponse> handleAccessExpiredException(
           AccessExpiredException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.FORBIDDEN, "ACCESS_OPERATION");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
   }

   @ExceptionHandler(RenewSubscriptionException.class)
   public ResponseEntity<ApiErrorResponse> handleRenewSubscriptionException(
           RenewSubscriptionException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.CONFLICT, "UPDATE_OPERATION");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
   }

   @ExceptionHandler(EntityNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(
           EntityNotFoundException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND_OPERATION");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
   }

   @ExceptionHandler(IllegalSubjectAuthenticatedException.class)
   public ResponseEntity<ApiErrorResponse> handleIllegalSubjectAuthenticatedException(
           IllegalSubjectAuthenticatedException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.FORBIDDEN, "AUTHORIZE_OPERATION");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
   }

   @ExceptionHandler(NoActiveException.class)
   public ResponseEntity<ApiErrorResponse> handleNoActiveException(
           NoActiveException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.CONFLICT, "SAVE_OPERATION");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
   }

   @ExceptionHandler(EmailNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleEmailNotFoundException(
           EmailNotFoundException ex,
           HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "UPDATE_OPERATION");
      return ResponseEntity.status(errorResponse.status()).body(errorResponse);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
           MethodArgumentNotValidException ex,
           HttpServletRequest request
   ) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "VALIDATION_OPERATION");
      return ResponseEntity.status(errorResponse.status()).body(errorResponse);
   }

   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
           ConstraintViolationException ex,
           HttpServletRequest request) {

      final var errorResponse = responseFactory.buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "CONSTRAINT_OPERATION");
      return ResponseEntity.status(errorResponse.status()).body(errorResponse);
   }
}