package com.jame.dev.gymApp.features.auth.application.contract.expiration;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;

public interface ExpirationService {
   void getMoreTimeFor(@EmailValid final String email);
}
