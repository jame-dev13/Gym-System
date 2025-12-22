package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.model.dto.auth.*;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import lombok.NonNull;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface AuthService {
   void signUp(final UserDtoInput dto) throws ExecutionException, InterruptedException;
   SignInOkDto signIn(final SignInDto dto);
   CookieResponseDto refresh(final String token);
   Optional<VerificationDto> verify(final String email, final String code);
   ExpirationWindowDto setNewExpiration(@NonNull final String email);
}
