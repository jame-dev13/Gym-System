package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.service.common.BaseService;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import jakarta.validation.constraints.Positive;

import java.util.Optional;

public interface CustomerService extends
   BaseService<CustomerDtoOutput, CustomerDtoInput>,
        EmailIdentifiable<CustomerEntity> {
   Optional<CustomerEntity> getUserByEmail(@EmailValid final String email);
   boolean exitsByIdAndCustomerEmail(@Positive final long id, @EmailValid final String email);
}
