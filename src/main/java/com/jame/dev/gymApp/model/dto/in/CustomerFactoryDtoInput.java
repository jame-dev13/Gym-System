package com.jame.dev.gymApp.model.dto.in;

import com.jame.dev.gymApp.entity.UserEntity;
import lombok.NonNull;

public record CustomerFactoryDtoInput(
        @NonNull UserEntity userEntity,
        @NonNull CustomerDtoInput dto
) {
}
