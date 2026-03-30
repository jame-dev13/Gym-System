package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.aspects.annotations.constraints.NoAdminRole;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotEmptyNull;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.auth.SignInOkDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;

public interface AuthService {
   void signUp(@NoAdminRole final UserDtoInput dto);

   SignInOkDto signIn(@NotNullObject final SignInDto dto);

   CookieResponseDto refresh(@NotEmptyNull final String token);
}
