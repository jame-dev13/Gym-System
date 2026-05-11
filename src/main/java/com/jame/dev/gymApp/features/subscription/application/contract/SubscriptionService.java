package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.application.contract.EmailIdentifiable;
import com.jame.dev.gymApp.application.contract.FullService;
import jakarta.validation.constraints.Positive;
import org.springframework.transaction.annotation.Transactional;

public interface SubscriptionService extends
   FullService<SubscriptionResponse, SubscriptionRequest>, EmailIdentifiable<SubscriptionEntity> {
   @Transactional(readOnly = true)
   boolean exitsByIdAndCustomerEmail(@Positive long id , @EmailValid final String email);
}
