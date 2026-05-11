package com.jame.dev.gymApp.features.user.application.contract;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.application.contract.BaseService;

import java.util.Optional;

public interface UserService extends
   BaseService<UserResponse, UserRequest> {
   Optional<UserEntity> getUserByEmail(@EmailValid final String email);
}
