package com.jame.dev.gymApp.features.user.application.contract;

import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.request.UserUpdateRequest;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;

public interface UserUpdater {
   void apply(UserEntity userEntity, UserUpdateRequest userUpdateRequest);
   void apply(UserEntity userEntity, UserRequest request);
}
