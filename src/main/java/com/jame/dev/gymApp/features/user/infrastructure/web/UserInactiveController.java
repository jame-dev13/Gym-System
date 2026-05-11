package com.jame.dev.gymApp.features.user.infrastructure.web;

import com.jame.dev.gymApp.infrastructure.web.InactiveController;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserInactiveService;
import org.springframework.stereotype.Component;

@Component
public class UserInactiveController extends InactiveController<UserMinimalInfoResponse> {
   public UserInactiveController(final UserInactiveService inactiveService) {
      super(inactiveService);
   }
}
