package com.jame.dev.gymApp.features.customer.application.usecases.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import org.springframework.data.domain.Pageable;

public interface GetPageCustomerUseCase {
    PageDto<CustomerResponse> getPage(final Pageable pageable, final String search);
}
