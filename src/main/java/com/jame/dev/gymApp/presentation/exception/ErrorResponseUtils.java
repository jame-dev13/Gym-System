package com.jame.dev.gymApp.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.Objects;

public final class ErrorResponseUtils {

   public static String extractMessage(final BindException bindException) {
      return bindException.getBindingResult()
         .getFieldErrors()
         .stream()
         .map(FieldError::getDefaultMessage)
         .filter(Objects::nonNull)
         .findFirst()
         .orElse("Unexpected value.");
   }

   public static String extractMessage(final ConstraintViolationException constraintViolationException) {
      return constraintViolationException.getConstraintViolations()
         .stream()
         .map(ConstraintViolation::getMessage)
         .filter(Objects::nonNull)
         .findFirst()
         .orElse("Unacceptable value reached.");
   }

   private ErrorResponseUtils() {
   }
}
