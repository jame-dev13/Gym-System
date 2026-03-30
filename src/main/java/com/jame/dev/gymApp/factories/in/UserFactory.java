package com.jame.dev.gymApp.factories.in;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;

public non-sealed interface UserFactory extends Factory<
        UserEntity, UserDtoOutput, UserDtoInput>{
}
