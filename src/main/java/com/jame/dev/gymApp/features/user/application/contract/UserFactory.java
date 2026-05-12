package com.jame.dev.gymApp.features.user.application.contract;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.application.support.factories.Factory;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import org.springframework.data.domain.Page;

public interface UserFactory extends Factory<
   UserEntity, UserResponse, UserRequest> {
   PageDto<UserMinimalInfoResponse> createMinimalInfoPage(final Page<UserMinimalInfoResponse> page);
}
