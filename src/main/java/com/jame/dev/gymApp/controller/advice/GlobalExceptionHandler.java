package com.jame.dev.gymApp.controller.advice;

import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.shared.enums.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
   private final ApiErrorResponseFactory responseFactory;

   @ExceptionHandler(UserNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleUserNotFoundException(final UserNotFoundException ex,
                                                                                final HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND
      ));
   }

   @ExceptionHandler(CustomerNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleCustomerNotFoundException(final CustomerNotFoundException ex,
                                                                                    final HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND
      ));
   }

   @ExceptionHandler(MembershipNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleMembershipNotFoundException(final MembershipNotFoundException ex,
                                                                                      final HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND
      ));
   }

   @ExceptionHandler(PeriodNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handlePeriodNotFoundException(final PeriodNotFoundException ex,
                                                                                  final HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND
      ));
   }

   @ExceptionHandler(PricingNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handlePricingNotFoundException(final PricingNotFoundException ex,
                                                                                   final HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND
      ));
   }

   @ExceptionHandler(SubscriptionNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleSubscriptionNotFoundException(final SubscriptionNotFoundException ex,
                                                                                        final HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND
      ));
   }

   @ExceptionHandler(ExtractClaimException.class)
   public ResponseEntity<ApiErrorResponse> handleExtractClaimException(
           ExtractClaimException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.EXTRACTION));
   }

   @ExceptionHandler(AuthenticationAttemptFailureException.class)
   public ResponseEntity<ApiErrorResponse> handleAuthenticationAttemptFailureException(
           AuthenticationAttemptFailureException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.UNAUTHORIZED, ErrorCodes.AUTHENTICATION));
   }

   @ExceptionHandler(AuthenticationNullException.class)
   public ResponseEntity<ApiErrorResponse> handleAuthenticationNullException(
           AuthenticationNullException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.UNAUTHORIZED, ErrorCodes.AUTHENTICATION));
   }

   @ExceptionHandler(CantSaveUserException.class)
   public ResponseEntity<ApiErrorResponse> handleCantSaveUserException(
           CantSaveUserException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.SAVE));
   }

   @ExceptionHandler(CantSaveVerifcationEntityException.class)
   public ResponseEntity<ApiErrorResponse> handleCantSaveVerificationEntityException(
           CantSaveVerifcationEntityException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.SAVE));
   }

   @ExceptionHandler(InvalidJwtException.class)
   public ResponseEntity<ApiErrorResponse> handleInvalidJwtException(
           InvalidJwtException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.FORBIDDEN, ErrorCodes.ACCESS_DENIED));
   }

   @ExceptionHandler(InvalidSignedJwtKeyException.class)
   public ResponseEntity<ApiErrorResponse> handleInvalidSignedJwtKeyException(
           InvalidSignedJwtKeyException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.FORBIDDEN, ErrorCodes.ACCESS_DENIED));
   }

   @ExceptionHandler(RoleNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleRoleNotFoundException(
           RoleNotFoundException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND));
   }

   @ExceptionHandler(TokenAlreadyBlacklistedException.class)
   public ResponseEntity<ApiErrorResponse> handleTokenAlreadyBlacklistedException(
           TokenAlreadyBlacklistedException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.SAVE));
   }

   @ExceptionHandler(VerificationTokenNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleVerificationTokenNotFoundException(
           VerificationTokenNotFoundException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND));
   }

   @ExceptionHandler(UserNotVerifiedException.class)
   public ResponseEntity<ApiErrorResponse> handleUserNotVerifiedException(
           UserNotVerifiedException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.FORBIDDEN, ErrorCodes.VERIFICATION));
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
           IllegalArgumentException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.BAD_REQUEST, ErrorCodes.ARGUMENT));
   }

   @ExceptionHandler(AlreadyExistsException.class)
   public ResponseEntity<ApiErrorResponse> handleAlreadyExistsException(
           AlreadyExistsException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.SAVE));
   }

   @ExceptionHandler(AccessExpiredException.class)
   public ResponseEntity<ApiErrorResponse> handleAccessExpiredException(
           AccessExpiredException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.FORBIDDEN, ErrorCodes.ACCESS));
   }

   @ExceptionHandler(RenewSubscriptionException.class)
   public ResponseEntity<ApiErrorResponse> handleRenewSubscriptionException(
           RenewSubscriptionException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.UPDATE));
   }

   @ExceptionHandler(EntityNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(
           EntityNotFoundException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND));
   }

   @ExceptionHandler(IllegalSubjectAuthenticatedException.class)
   public ResponseEntity<ApiErrorResponse> handleIllegalSubjectAuthenticatedException(
           IllegalSubjectAuthenticatedException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.UNAUTHORIZED, ErrorCodes.NO_ACCESS));
   }

   @ExceptionHandler(NoActiveException.class)
   public ResponseEntity<ApiErrorResponse> handleNoActiveException(
           NoActiveException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.ACCESS));
   }

   @ExceptionHandler(EmailNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleEmailNotFoundException(
           EmailNotFoundException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND));
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
           MethodArgumentNotValidException ex,
           HttpServletRequest request
   ) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION));
   }

   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
   public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
           MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.BAD_REQUEST, ErrorCodes.TYPE));
   }

   @ExceptionHandler(HttpMessageNotReadableException.class)
   public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
           HttpMessageNotReadableException ex, HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION));
   }

   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
           ConstraintViolationException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.BAD_REQUEST, ErrorCodes.CONSTRAINT));
   }

   @ExceptionHandler(NonLocalAuthenticationAllowedException.class)
   public ResponseEntity<ApiErrorResponse> handleNonLocalAuthenticationAllowedException(
           NonLocalAuthenticationAllowedException ex,
           HttpServletRequest request) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.UNAUTHORIZED, ErrorCodes.SAVE));
   }

   @ExceptionHandler(VerificationNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleVerificationNotFoundException(
           VerificationNotFoundException ex,
           HttpServletRequest request
   ) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND));
   }

   @ExceptionHandler(SubscriptionUnfinishedException.class)
   public ResponseEntity<ApiErrorResponse> handleSubscriptionUnfinishedException(
           SubscriptionUnfinishedException ex,
           HttpServletRequest request
   ) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.UPDATE));
   }

   @ExceptionHandler(MissMatchException.class)
   public ResponseEntity<ApiErrorResponse> handleMissMatchException(
           MissMatchException ex,
           HttpServletRequest request
   ) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.UPDATE));
   }

   @ExceptionHandler(VerificationAttemptFailedException.class)
   public ResponseEntity<ApiErrorResponse> handleVerificationAttemptFailedException(
           VerificationAttemptFailedException ex,
           HttpServletRequest request
   ) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.BAD_REQUEST, ErrorCodes.UPDATE));
   }

   @ExceptionHandler(AlreadyVerifiedException.class)
   public ResponseEntity<ApiErrorResponse> handleAlreadyVerifiedException(
           AlreadyVerifiedException ex,
           HttpServletRequest request
   ) {
      return responseFactory.buildResponse(new InputError(
              ex, request, HttpStatus.CONFLICT, ErrorCodes.VALIDATION));
   }

   @ExceptionHandler(WindowTimeException.class)
   public ResponseEntity<ApiErrorResponse> handleWindowVerifiedException(
           WindowTimeException ex, HttpServletRequest request
   ) {
      return responseFactory
              .buildResponse(new InputError(
                      ex, request, HttpStatus.BAD_REQUEST, ErrorCodes.ACCESS));
   }


   @ExceptionHandler(AccountNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleAccountNotFoundException(
           AccountNotFoundException ex, HttpServletRequest request
   ) {
      return responseFactory
              .buildResponse(new InputError(
                      ex, request, HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND));
   }

   @ExceptionHandler(AuthProviderNotAllowedException.class)
   public ResponseEntity<ApiErrorResponse> handleAuthProviderNotAllowedException(
           AuthProviderNotAllowedException ex, HttpServletRequest request
   ) {
      return responseFactory
              .buildResponse(new InputError(
                      ex, request, HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION));
   }

   @ExceptionHandler(TooManyRequestsException.class)
   public ResponseEntity<ApiErrorResponse> handleTooManyRequestsException(
           TooManyRequestsException ex, HttpServletRequest request
   ) {
      return responseFactory
              .buildResponse(new InputError(
                      ex, request, HttpStatus.TOO_MANY_REQUESTS, ErrorCodes.ACCESS));
   }


   @ExceptionHandler(TemporaryBlockedException.class)
   public ResponseEntity<ApiErrorResponse> handleTemporaryBlockedException(
           TemporaryBlockedException ex, HttpServletRequest request
   ) {
      return responseFactory
              .buildResponse(new InputError(
                      ex, request, HttpStatus.LOCKED, ErrorCodes.ACCESS_DENIED));
   }
}