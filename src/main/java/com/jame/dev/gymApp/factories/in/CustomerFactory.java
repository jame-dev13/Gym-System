package com.jame.dev.gymApp.factories.in;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;

public non-sealed interface CustomerFactory extends Factory<
        CustomerEntity, CustomerDtoOutput, CustomerFactoryDtoInput> {
}
