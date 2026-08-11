package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.application.contract.LogoutService;
import com.jame.dev.gymApp.features.auth.application.model.CookieNames;
import com.jame.dev.gymApp.features.auth.application.support.helper.CookieHelper;
import com.jame.dev.gymApp.infrastructure.cache.BlacklistService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogoutApplicationService implements LogoutService {

   private final BlacklistService blacklistService;
   private final CookieHelper cookieHelper;

   @Override
   @AuditLog(
      action = AuditLogAction.LOGOUT,
      entityType = AuditLogEntityType.AUTHENTICATION,
      input = "#request"
   )
   public void logout(HttpServletRequest request, HttpServletResponse response) {
      if (request.getCookies() == null)
         throw new IllegalArgumentException("Logout already made.");

      Arrays.stream(request.getCookies())
         .collect(Collectors.toMap(Cookie::getName, Cookie::getValue))
         .forEach((name, value) -> {
            if (name.equals(CookieNames.COOKIE_JWT_REFRESH.getValue())) {
               blacklistService.blacklistToken(value);
            }
            cookieHelper.clearCookie(response, name);
         });

   }
}
