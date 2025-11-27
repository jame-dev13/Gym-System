package com.jame.dev.gymApp.config.web;

import com.jame.dev.gymApp.shared.enums.CookieNames;
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

   public ResponseCookie clearCookie(final String name){
      return ResponseCookie.from(name, "")
              .httpOnly(true)
              .secure(false)
              .path("/")
              .maxAge(0)
              .build();
   }

   private ResponseCookie buildCookie(final String name, final String value, final long exp){
      return ResponseCookie.from(name, value)
              .httpOnly(true)
              .secure(true)
              .path("/")
              .maxAge(Duration.ofMinutes(exp))
              .build();
   }
}
