package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.constraints.NotEmptyNull;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.model.dto.auth.SessionDto;
import org.springframework.security.core.Authentication;

public interface SessionService {
   SessionDto getSession(
           @NotEmptyNull String access,
           @NotNullObject Authentication authentication);
}
