package com.jame.dev.gymApp.model.dto.in;

import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.entity.UserEntity;

public record CustomerFactoryDtoInput(
        @NotNullObject
        UserEntity userEntity,
        @NotNullObject
        CustomerDtoInput dto
) {
}
