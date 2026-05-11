package com.jame.dev.gymApp.infrastructure.config.web;

import com.jame.dev.gymApp.features.auth.application.model.CookieNames;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@NoArgsConstructor
public class CookieHelper {

   @Value("${jwt.secret.expiration}")
   private long expirationAccess;
   @Value("${jwt.refresh.expiration}")
   private long expirationRefresh;

   public ResponseCookie createAccessTokenCookie(final String value){
      return buildCookie(CookieNames.COOKIE_JWT_ACCESS.getValue(), value, expirationAccess);
   }

   public ResponseCookie createRefreshTokenCookie(final String value){
      return buildCookie(CookieNames.COOKIE_JWT_REFRESH.getValue(), value, expirationRefresh);
   }

   public void clearCookie(final HttpServletResponse response, final String name){
      final ResponseCookie cookie = ResponseCookie.from(name, "")
              .httpOnly(true)
              .sameSite("none")
              .secure(true)
              .path("/")
              .maxAge(0)
              .build();
      response.addHeader("Set-Cookie", cookie.toString());
   }

   private ResponseCookie buildCookie(final String name, final String value, final long exp){
      return ResponseCookie.from(name, value)
              .httpOnly(true)
              .sameSite("none")
              .secure(true)
              .path("/")
              .maxAge(Duration.ofMillis(exp))
              .build();
   }
}
