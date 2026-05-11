package com.jame.dev.gymApp.features.auth.application.contract.verification;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;

public interface VerificationService {
   VerificationEntity save(@Minimum final long userId, @NotEmptyNull final String token);
   void verify(@EmailValid final String email, @NotEmptyNull final String rawToken);
   void update(final String email, final String rawToken);
   //void delete(@NonNull final String token);
   boolean isVerified(@EmailValid final String email);
   boolean checkVerifiedDeactivated(@EmailValid final String email);
   boolean verificationExistsFor(final String email);
}
