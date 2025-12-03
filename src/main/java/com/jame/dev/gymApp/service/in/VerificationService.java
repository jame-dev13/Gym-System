package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import lombok.NonNull;

public interface VerificationService {
   VerificationEntity save(@NonNull final UserEntity user);
   boolean verify(@NonNull final String token);
   void delete(@NonNull final String token);
}
