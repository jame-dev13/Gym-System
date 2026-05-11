package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.auth.application.contract.LogoutService;
import com.jame.dev.gymApp.infrastructure.cache.BlacklistService;
import com.jame.dev.gymApp.infrastructure.config.web.CookieHelper;
import com.jame.dev.gymApp.features.auth.application.model.CookieNames;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogoutApplicationService implements LogoutService {

   private final BlacklistService blacklistService;
   private final CookieHelper cookieHelper;

   @Override
   public void logout(HttpServletRequest request, HttpServletResponse response) {
      if (request.getCookies() == null) {
         throw new IllegalArgumentException("Logout already made.");
      }
      final Cookie[] cookies = request.getCookies();
      final Map<String, String> cookieMap = Arrays.stream(cookies)
              .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

      cookieMap.forEach((name, value) -> {
         if (name.equals(CookieNames.COOKIE_JWT_REFRESH.getValue())) {
            blacklistService.blacklistToken(value);
         }
         cookieHelper.clearCookie(response, name);
      });
   }
}
