package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.shared.enums.CookieNames;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogoutServiceImplementation implements LogoutService {

   private final BlacklistService blacklistService;
   private final CookieHelper cookieHelper;

   @Override
   public void logout(HttpServletRequest request) {
      final Map<String, String> cookies = Arrays.stream(request.getCookies())
              .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

      cookies.forEach((name, value) -> {
         if (name.equals(CookieNames.COOKIE_JWT_REFRESH.getValue())) {
            blacklistService.blacklistToken(value);
         }
         cookieHelper.clearCookie(name);
      });
   }
}
