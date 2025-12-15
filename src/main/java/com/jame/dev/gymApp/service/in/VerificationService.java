package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.model.dto.auth.ExpirationWindowDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import lombok.NonNull;

public interface VerificationService {
   VerificationEntity save(@NonNull final UserEntity user);
   VerificationDto verify(@NonNull final String email, @NonNull final String token);
   ExpirationWindowDto getMoreExpTime(@NonNull final String email);
   void delete(@NonNull final String token);
   boolean isVerified(final String email);
}
