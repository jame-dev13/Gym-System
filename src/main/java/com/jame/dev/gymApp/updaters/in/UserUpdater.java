package com.jame.dev.gymApp.updaters.in;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.updaters.common.Updatable;

public interface UserUpdater extends Updatable<UserEntity, UserDtoInput> {
}
