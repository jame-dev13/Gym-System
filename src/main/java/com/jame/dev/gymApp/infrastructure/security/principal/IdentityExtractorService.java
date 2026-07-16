package com.jame.dev.gymApp.infrastructure.security.principal;

import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import org.springframework.security.core.Authentication;

public interface IdentityExtractorService {
   String extract(@NotNullObject final Authentication authentication);
}
