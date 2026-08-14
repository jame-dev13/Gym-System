package com.jame.dev.gymApp.features.subscription.application.usecases.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.subscription.api.response.PaymentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface GetPaymentByCurrent {
   PageDto<PaymentResponse> getPaymentByCurrent(final Authentication authentication, final String search, final Pageable pageable);
}
