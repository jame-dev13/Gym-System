package com.jame.dev.gymApp.updaters.in;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.updaters.common.Updatable;

public interface CustomerUpdater extends Updatable<CustomerEntity, CustomerDtoInput> {
}
