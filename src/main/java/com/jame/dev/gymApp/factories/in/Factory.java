package com.jame.dev.gymApp.factories.in;

import com.jame.dev.gymApp.model.dto.out.PageDto;
import org.springframework.data.domain.Page;

public sealed interface Factory<E, DTO_OUT, DTO_IN> permits CustomerFactory, SubscriptionFactory, UserFactory {
   PageDto<DTO_OUT> createPageFrom(final Page<E> page);
   DTO_OUT createFromEntity(final E entity);
   E createFromInput(DTO_IN input);
}
