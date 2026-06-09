package com.jame.dev.gymApp.features.user.application.usecases.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface GetPageUserUseCase {
   PageDto<UserResponse> getPage(final Pageable pageable, final String search);
   PageDto<UserMinimalInfoResponse> getInactivePage(final Pageable pageable, final String search);
}
