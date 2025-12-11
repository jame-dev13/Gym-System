package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;

public interface SubscriptionService extends CRUDServiceServicePatch<SubscriptionEntity, SubscriptionDtoInput, Long> {
}
