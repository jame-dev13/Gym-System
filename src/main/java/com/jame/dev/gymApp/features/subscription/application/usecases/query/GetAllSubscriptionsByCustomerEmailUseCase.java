package com.jame.dev.gymApp.features.subscription.application.usecases.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import org.springframework.data.domain.Pageable;

public interface GetAllSubscriptionsByCustomerEmailUseCase {
   PageDto<SubscriptionResponse> getAllByCustomerEmail(final String customerEmail, final Pageable pageable);
}
