package com.jame.dev.gymApp.application.contract;

import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import org.springframework.security.core.Authentication;

public interface IdentityExtractorService {
   String extract(@NotNullObject final Authentication authentication);
}
