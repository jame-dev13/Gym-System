package com.jame.dev.gymApp.features.user.application.usecases.mutation;

import com.jame.dev.gymApp.features.user.api.request.UserUpdateRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;

public interface UpdateUserUseCase {
   UserResponse update(final long id, final UserUpdateRequest request);
}
