package com.jame.dev.gymApp.controller.advice;

import com.jame.dev.gymApp.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
   public ResponseEntity<@NonNull ApiErrorResponse> handleNoOperationException(final NoOperationException ex,
                                                                                        final HttpServletRequest request) {
      final ApiErrorResponse errorResponse = responseFactory
              .buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "UNSUPPORTED_OPERATION");
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(errorResponse);
   }
}
