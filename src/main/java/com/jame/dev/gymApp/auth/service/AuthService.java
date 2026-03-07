package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.auth.SignInOkDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;

import java.util.concurrent.ExecutionException;

public interface AuthService {
   void signUp(final UserDtoInput dto) throws ExecutionException, InterruptedException;

   SignInOkDto signIn(final SignInDto dto);

   CookieResponseDto refresh(final String token);
}
