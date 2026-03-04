package com.jame.dev.gymApp.shared.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;

public final class ErrorResponseUtils {

   public static String extractMessage(final BindException bindException) {
      return bindException.getBindingResult()
              .getFieldErrors()
              .stream()
              .map(FieldError::getDefaultMessage)
              .collect(Collectors.joining(" "));
   }

   public static String extractMessage(final ConstraintViolationException constraintViolationException) {
      return constraintViolationException.getConstraintViolations()
              .stream()
              .map(ConstraintViolation::getMessage)
              .collect(Collectors.joining(" "));
   }
}
