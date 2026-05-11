package com.jame.dev.gymApp.features.auth.application.contract;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.auth.api.request.TokenIdResetPasswordRequest;
import com.jame.dev.gymApp.features.auth.api.request.PasswordResetRequest;

public interface OneTimeTokenService {
   void saveToken(final String rawToken, final UserEntity user);

   void validateTokenRequest(
      final TokenIdResetPasswordRequest tokenIdResetPasswordRequest);

   void resetPassword(final PasswordResetRequest passwordResetRequest);
}
