package com.jame.dev.gymApp.service.in;

import org.springframework.security.core.Authentication;

public interface IdentityExtractorService {
   String extract(Authentication authentication);
}
