package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;

public interface SubscriptionService extends
        CRUDServiceServicePatch<SubscriptionEntity, SubscriptionDtoInput, Long>,
        CRUDServiceServicePut<SubscriptionEntity, SubscriptionDtoInput, Long>,
        EmailIdentifiable<SubscriptionEntity> {
   boolean exitsByIdAndCustomerEmail(long id, String email);
}
