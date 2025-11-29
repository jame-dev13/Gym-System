package com.jame.dev.gymApp.auth.handlers;

import com.jame.dev.gymApp.auth.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

   private final LogoutService logoutService;

   @Override
   public void logout(HttpServletRequest request,
                      HttpServletResponse response,
                      @Nullable Authentication authentication) {
      logoutService.logout(request);
   }
}
