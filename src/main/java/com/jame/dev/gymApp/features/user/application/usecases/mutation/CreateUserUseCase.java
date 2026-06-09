package com.jame.dev.gymApp.features.user.application.usecases.mutation;

import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;

public interface CreateUserUseCase {
   UserResponse create(final UserRequest request);
}
