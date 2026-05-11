package com.jame.dev.gymApp.features.customer.application.contract;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.application.contract.Updatable;

public interface CustomerUpdater extends Updatable<CustomerEntity, CustomerRequest> {
}
