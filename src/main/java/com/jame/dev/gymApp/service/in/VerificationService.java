package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotEmptyNull;
import com.jame.dev.gymApp.entity.VerificationEntity;

public interface VerificationService {
   VerificationEntity save(@Minimum final long userId, @NotEmptyNull final String token);
   void verify(@EmailValid final String email, @NotEmptyNull final String rawToken);
   void update(final String email, final String rawToken);
   //void delete(@NonNull final String token);
   boolean isVerified(@EmailValid final String email);
   boolean checkVerifiedDeactivated(@EmailValid final String email);
}
