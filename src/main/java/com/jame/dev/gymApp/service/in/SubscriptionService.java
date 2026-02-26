package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import jakarta.validation.constraints.Positive;

public interface SubscriptionService extends
        CRUDServiceServicePatch<SubscriptionDtoOutput, SubscriptionDtoInput>,
        CRUDServiceServicePut<SubscriptionDtoOutput, SubscriptionDtoInput>,
        EmailIdentifiable<SubscriptionEntity> {
   boolean exitsByIdAndCustomerEmail(@Positive long id, @EmailValid final String email);
}
