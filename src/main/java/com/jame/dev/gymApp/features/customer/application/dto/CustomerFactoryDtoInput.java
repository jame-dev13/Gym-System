package com.jame.dev.gymApp.features.customer.application.dto;

import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;

public record CustomerFactoryDtoInput(
        @NotNullObject
        UserEntity userEntity,
        @NotNullObject
        CustomerRequest dto
) {
}
