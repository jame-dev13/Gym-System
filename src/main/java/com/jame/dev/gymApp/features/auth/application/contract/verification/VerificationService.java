package com.jame.dev.gymApp.features.auth.application.contract.verification;

import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;
import jakarta.validation.constraints.NotNull;

public interface VerificationService {
   VerificationEntity save(@NotNull UserEntity user, @NotEmptyNull final String token);
   void verify(@EmailValid final String email, @NotEmptyNull final String rawToken);
   VerificationEntity getByUserEmail(@EmailValid final String email);
   VerificationEntity getByDeactivatedUserEmail(final String email);
   void verify(@NotNull VerificationEntity verificationEntity, String rawToken);
   void update(final VerificationEntity entity, final String rawToken);
   boolean isVerified(@EmailValid final String email);
   boolean checkVerifiedDeactivated(@EmailValid final String email);
   boolean verificationExistsFor(final String email);
}
