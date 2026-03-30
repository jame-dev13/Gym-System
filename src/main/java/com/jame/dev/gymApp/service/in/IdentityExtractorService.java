package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import org.springframework.security.core.Authentication;

public interface IdentityExtractorService {
   String extract(@NotNullObject final Authentication authentication);
}
