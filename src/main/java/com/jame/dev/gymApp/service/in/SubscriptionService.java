package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.common.ServiceComplex;
import jakarta.validation.constraints.Positive;

public interface SubscriptionService extends
        ServiceComplex<SubscriptionDtoOutput, SubscriptionDtoInput, SubscriptionEntity> {
   boolean exitsByIdAndCustomerEmail(@Positive long id, @EmailValid final String email);
}
