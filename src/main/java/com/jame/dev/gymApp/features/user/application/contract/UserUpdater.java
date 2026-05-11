package com.jame.dev.gymApp.features.user.application.contract;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.application.contract.Updatable;

public interface UserUpdater extends Updatable<UserEntity, UserRequest> {
}
