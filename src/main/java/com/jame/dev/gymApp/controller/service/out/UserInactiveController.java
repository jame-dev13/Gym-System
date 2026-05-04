package com.jame.dev.gymApp.controller.service.out;

import com.jame.dev.gymApp.controller.service.InactiveController;
import com.jame.dev.gymApp.model.dto.out.UserMinimalInfo;
import com.jame.dev.gymApp.service.in.UserInactiveService;
import org.springframework.stereotype.Component;

@Component
public class UserInactiveController extends InactiveController<UserMinimalInfo> {
   public UserInactiveController(final UserInactiveService inactiveService) {
      super(inactiveService);
   }
}
