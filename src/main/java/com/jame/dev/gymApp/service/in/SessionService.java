package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.model.dto.auth.SessionDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;

public interface SessionService {
   SessionDto getSession(
           @NotNull(message = "Access cookie no present.") String access,
           @NotNull(message = "Authentication null.") Authentication authentication);
}
