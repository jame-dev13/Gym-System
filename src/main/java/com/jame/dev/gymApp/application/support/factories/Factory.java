package com.jame.dev.gymApp.application.support.factories;

import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.application.dto.PageDto;
import org.springframework.data.domain.Page;

public sealed interface Factory<E, DTO_OUT, DTO_IN> permits CustomerFactory, SubscriptionFactory, UserFactory {
   PageDto<DTO_OUT> createPageFrom(final Page<E> page);
   DTO_OUT createFromEntity(final E entity);
   E createFromInput(DTO_IN input);
}
