package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;

public interface AuthService {
   void signUp(final UserDtoInput dto);
   CookieResponseDto signIn(final SignInDto dto);
   CookieResponseDto refresh(final String token);
}
