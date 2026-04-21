package com.jame.dev.gymApp.model.dto.out;

import com.jame.dev.gymApp.shared.enums.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public record Error(
   Throwable ex,
   HttpServletRequest request,
   HttpStatus httpStatusCode,
   ErrorCodes errorCode
) {
}
