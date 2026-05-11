package com.jame.dev.gymApp.presentation.exception;

import com.jame.dev.gymApp.application.model.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public record InputError(
        Throwable ex,
        HttpServletRequest request,
        HttpStatus httpStatusCode,
        ErrorCodes errorCode
) {
}
