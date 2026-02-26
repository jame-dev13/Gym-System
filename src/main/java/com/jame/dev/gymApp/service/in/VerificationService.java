package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.model.dto.auth.ExpirationWindowDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;

public interface VerificationService {
   VerificationEntity save(@Positive final long userId);
   VerificationDto verify(@EmailValid final String email, @NotEmptyNull final String token);
   ExpirationWindowDto getMoreExpTime(@EmailValid final String email);
   void delete(@NonNull final String token);
   boolean isVerified(@EmailValid final String email);
}
