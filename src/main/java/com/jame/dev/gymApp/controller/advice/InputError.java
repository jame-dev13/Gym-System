package com.jame.dev.gymApp.controller.advice;

import com.jame.dev.gymApp.shared.enums.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public record InputError(
        Throwable ex,
        HttpServletRequest request,
        HttpStatus httpStatusCode,
        ErrorCodes errorCode
) {
}
