package com.jame.dev.gymApp.presentation.exception;

import com.jame.dev.gymApp.domain.exception.*;
import com.jame.dev.gymApp.features.audit.domain.exception.AuditLogNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.*;
import com.jame.dev.gymApp.features.backup.domain.exception.BackupException;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.notification.domain.exception.SubscriberException;
import com.jame.dev.gymApp.features.subscription.domain.exception.*;
import com.jame.dev.gymApp.features.user.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
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
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(CustomerNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleCustomerNotFoundException(final CustomerNotFoundException ex,
                                                                                    final HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(MembershipNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleMembershipNotFoundException(final MembershipNotFoundException ex,
                                                                                      final HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(PeriodNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handlePeriodNotFoundException(final PeriodNotFoundException ex,
                                                                                  final HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(PricingNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handlePricingNotFoundException(final PricingNotFoundException ex,
                                                                                   final HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(SubscriptionNotFoundException.class)
   public ResponseEntity<@NonNull ApiErrorResponse> handleSubscriptionNotFoundException(final SubscriptionNotFoundException ex,
                                                                                        final HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(ExtractClaimException.class)
   public ResponseEntity<ApiErrorResponse> handleExtractClaimException(
      ExtractClaimException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.CLAIM_EXTRACTION_CONFLICT, ex, request);
   }

   @ExceptionHandler(AuthenticationAttemptFailureException.class)
   public ResponseEntity<ApiErrorResponse> handleAuthenticationAttemptFailureException(
      AuthenticationAttemptFailureException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.AUTHENTICATION_FAILURE, ex, request);
   }

   @ExceptionHandler(AuthenticationNullException.class)
   public ResponseEntity<ApiErrorResponse> handleAuthenticationNullException(
      AuthenticationNullException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.AUTHENTICATION_FAILURE, ex, request);
   }

   @ExceptionHandler(CantSaveUserException.class)
   public ResponseEntity<ApiErrorResponse> handleCantSaveUserException(
      CantSaveUserException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.SAVE_CONFLICT, ex, request);
   }

   @ExceptionHandler(CantSaveVerifcationEntityException.class)
   public ResponseEntity<ApiErrorResponse> handleCantSaveVerificationEntityException(
      CantSaveVerifcationEntityException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.SAVE_CONFLICT, ex, request);
   }

   @ExceptionHandler(InvalidJwtException.class)
   public ResponseEntity<ApiErrorResponse> handleInvalidJwtException(
      InvalidJwtException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.ACCESS_DENIED, ex, request);
   }

   @ExceptionHandler(InvalidSignedJwtKeyException.class)
   public ResponseEntity<ApiErrorResponse> handleInvalidSignedJwtKeyException(
      InvalidSignedJwtKeyException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.ACCESS_DENIED, ex, request);
   }

   @ExceptionHandler(RoleNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleRoleNotFoundException(
      RoleNotFoundException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(TokenAlreadyBlacklistedException.class)
   public ResponseEntity<ApiErrorResponse> handleTokenAlreadyBlacklistedException(
      TokenAlreadyBlacklistedException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.SAVE_CONFLICT, ex, request);
   }

   @ExceptionHandler(VerificationTokenNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleVerificationTokenNotFoundException(
      VerificationTokenNotFoundException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(UserNotVerifiedException.class)
   public ResponseEntity<ApiErrorResponse> handleUserNotVerifiedException(
      UserNotVerifiedException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.VERIFICATION_REQUIRED, ex, request);
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.ILLEGAL_ARGUMENT, ex, request);
   }

   @ExceptionHandler(AlreadyExistsException.class)
   public ResponseEntity<ApiErrorResponse> handleAlreadyExistsException(
      AlreadyExistsException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.SAVE_CONFLICT, ex, request);
   }

   @ExceptionHandler(AccessExpiredException.class)
   public ResponseEntity<ApiErrorResponse> handleAccessExpiredException(
      AccessExpiredException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.ACCESS_EXPIRED, ex, request);
   }

   @ExceptionHandler(RenewSubscriptionException.class)
   public ResponseEntity<ApiErrorResponse> handleRenewSubscriptionException(
      RenewSubscriptionException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.UPDATE_CONFLICT, ex, request);
   }

   @ExceptionHandler(EntityNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(
      EntityNotFoundException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(IllegalSubjectAuthenticatedException.class)
   public ResponseEntity<ApiErrorResponse> handleIllegalSubjectAuthenticatedException(
      IllegalSubjectAuthenticatedException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.NO_ACCESS, ex, request);
   }

   @ExceptionHandler(NoActiveException.class)
   public ResponseEntity<ApiErrorResponse> handleNoActiveException(
      NoActiveException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.INACTIVE_STATE_CONFLICT, ex, request);
   }

   @ExceptionHandler(EmailNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleEmailNotFoundException(
      EmailNotFoundException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.VALIDATION_FAILURE, ex, request);
   }

   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
   public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.TYPE_MISMATCH, ex, request);
   }

   @ExceptionHandler(HttpMessageNotReadableException.class)
   public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.VALIDATION_FAILURE, ex, request);
   }

   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.CONSTRAINT_VIOLATION, ex, request);
   }

   @ExceptionHandler(NonLocalAuthenticationAllowedException.class)
   public ResponseEntity<ApiErrorResponse> handleNonLocalAuthenticationAllowedException(
      NonLocalAuthenticationAllowedException ex,
      HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.EXTERNAL_AUTH_UNSUPPORTED, ex, request);
   }

   @ExceptionHandler(VerificationNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleVerificationNotFoundException(
      VerificationNotFoundException ex,
      HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(SubscriptionUnfinishedException.class)
   public ResponseEntity<ApiErrorResponse> handleSubscriptionUnfinishedException(
      SubscriptionUnfinishedException ex,
      HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.UPDATE_CONFLICT, ex, request);
   }

   @ExceptionHandler(StripeSessionException.class)
   public ResponseEntity<ApiErrorResponse> handleStripeSessionCreationException(
      StripeSessionException ex,
      HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.INTERNAL_FAILURE, ex, request);
   }

   @ExceptionHandler(MissMatchException.class)
   public ResponseEntity<ApiErrorResponse> handleMissMatchException(
      MissMatchException ex,
      HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.UPDATE_CONFLICT, ex, request);
   }

   @ExceptionHandler(VerificationAttemptFailedException.class)
   public ResponseEntity<ApiErrorResponse> handleVerificationAttemptFailedException(
      VerificationAttemptFailedException ex,
      HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.VERIFICATION_ATTEMPTS_EXHAUSTED, ex, request);
   }

   @ExceptionHandler(AlreadyVerifiedException.class)
   public ResponseEntity<ApiErrorResponse> handleAlreadyVerifiedException(
      AlreadyVerifiedException ex,
      HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.INACTIVE_STATE_CONFLICT, ex, request);
   }

   @ExceptionHandler(WindowTimeException.class)
   public ResponseEntity<ApiErrorResponse> handleWindowVerifiedException(
      WindowTimeException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.VERIFICATION_WINDOW_EXPIRED, ex, request);
   }


   @ExceptionHandler(AccountNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleAccountNotFoundException(
      AccountNotFoundException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(AuthProviderNotAllowedException.class)
   public ResponseEntity<ApiErrorResponse> handleAuthProviderNotAllowedException(
      AuthProviderNotAllowedException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.VALIDATION_FAILURE, ex, request);
   }

   @ExceptionHandler(TooManyRequestsException.class)
   public ResponseEntity<ApiErrorResponse> handleTooManyRequestsException(
      TooManyRequestsException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.TOO_MANY_REQUESTS, ex, request);
   }


   @ExceptionHandler(TemporaryBlockedException.class)
   public ResponseEntity<ApiErrorResponse> handleTemporaryBlockedException(
      TemporaryBlockedException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.ACCOUNT_LOCKED, ex, request);
   }

   @ExceptionHandler(OTTNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleOTTNotFoundException(
      OTTNotFoundException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(UserEntityNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleUserEntityNotFoundException(
      UserEntityNotFoundException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(UnverifiedOTTException.class)
   public ResponseEntity<ApiErrorResponse> handleUnverifiedOTTException(
      UnverifiedOTTException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.UNTRUSTED_TOKEN, ex, request);
   }

   @ExceptionHandler(MissingRequestCookieException.class)
   public ResponseEntity<ApiErrorResponse> handleMissingRequestCookieException(
      MissingRequestCookieException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.ILLEGAL_ARGUMENT, ex, request);
   }

   @ExceptionHandler(InvalidAuthenticationPrincipalException.class)
   public ResponseEntity<ApiErrorResponse> handleInvalidAuthenticationPrincipalException(
      InvalidAuthenticationPrincipalException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.AUTHENTICATION_FAILURE, ex, request);
   }

   @ExceptionHandler(AuditLogNotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleAuditLogNotFoundException(
      AuditLogNotFoundException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(NotificationException.class)
   public ResponseEntity<ApiErrorResponse> handleNotificationException(
      NotificationException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.NOTIFICATION_UNAVAILABLE, ex, request);
   }

   @ExceptionHandler(EventPublisherException.class)
   public ResponseEntity<ApiErrorResponse> handleEventPublisherException(
      EventPublisherException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.OPERATION_UNSUPPORTED, ex, request);
   }

   @ExceptionHandler(StateException.class)
   public ResponseEntity<ApiErrorResponse> handleStateException(
      StateException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.OPERATION_UNSUPPORTED, ex, request);
   }

   @ExceptionHandler(NotFoundException.class)
   public ResponseEntity<ApiErrorResponse> handleNotFoundException(
      NotFoundException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.RESOURCE_NOT_FOUND, ex, request);
   }

   @ExceptionHandler(BackupException.class)
   public ResponseEntity<ApiErrorResponse> handleBackupException(
      BackupException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.OPERATION_UNSUPPORTED, ex, request);
   }

   @ExceptionHandler(LockException.class)
   public ResponseEntity<ApiErrorResponse> handleLockException(
      LockException ex, HttpServletRequest request
   ) {
      return responseFactory.of(ApiErrorKind.OPERATION_UNSUPPORTED, ex, request);
   }

   @ExceptionHandler(NullPointerException.class)
   public ResponseEntity<ApiErrorResponse> handleNPE(NullPointerException ex, HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.INTERNAL_FAILURE, ex, request);
   }

   @ExceptionHandler(UnrelatedDataAccessException.class)
   public ResponseEntity<ApiErrorResponse> handleUnrelatedDataAccessException(UnrelatedDataAccessException ex, HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.DATA_ACCESS_FORBIDDEN, ex, request);
   }

   @ExceptionHandler(SubscriberException.class)
   public ResponseEntity<ApiErrorResponse> handleSubscriberException(SubscriberException ex, HttpServletRequest request) {
      return responseFactory.of(ApiErrorKind.OPERATION_UNSUPPORTED, ex, request);
   }
}
