package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.Minimum;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;

public interface VerificationService {
   VerificationEntity save(@Minimum final long userId, @NotEmptyNull final String token);
   VerificationDto verify(@EmailValid final String email, @NotEmptyNull final String rawToken);
   void update(final String email, final String rawToken);
   //void delete(@NonNull final String token);
   boolean isVerified(@EmailValid final String email);
   boolean checkVerifiedDeactivated(@EmailValid final String email);
}
