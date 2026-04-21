package com.jame.dev.gymApp.auth.handlers;

import com.jame.dev.gymApp.auth.service.LogoutService;
import com.jame.dev.gymApp.service.in.TryCatchBlockExecutorService;
import com.jame.dev.gymApp.shared.interfaces.VoidBlock;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

   private final LogoutService logoutService;
   private final TryCatchBlockExecutorService blockExecutorService;

   @Override
   public void logout(@NonNull HttpServletRequest request,
                      @NonNull HttpServletResponse response,
                      @Nullable Authentication authentication) {
      final VoidBlock voidBlock = () -> logoutService.logout(request, response);
      blockExecutorService.executeVoidBlock(request, response, voidBlock);
   }
}
