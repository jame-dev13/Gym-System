package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.auth.TokenIdResetPasswordRequest;
import com.jame.dev.gymApp.model.dto.in.PasswordResetDtoInput;

public interface OneTimeTokenService {
   void saveToken(final String rawToken, final UserEntity user);

   void validateTokenRequest(
      final TokenIdResetPasswordRequest tokenIdResetPasswordRequest);

   void resetPassword(final PasswordResetDtoInput passwordResetDtoInput);
}
