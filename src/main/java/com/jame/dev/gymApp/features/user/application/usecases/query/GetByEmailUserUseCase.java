package com.jame.dev.gymApp.features.user.application.usecases.query;

import com.jame.dev.gymApp.features.user.api.response.UserResponse;

public interface GetByEmailUserUseCase {
   UserResponse getByEmail(String email);
}
