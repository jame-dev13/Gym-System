package com.jame.dev.gymApp.auth.handlers;

import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.exception.AuthenticationNullException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationHandler implements AuthenticationSuccessHandler {

   private final JwtService jwt;
   private final CookieHelper cookieHelper;

   @Override
   public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {

      if(Objects.isNull(authentication.getPrincipal())){
         throw new AuthenticationNullException("No principal in authentication.");
      }
      final String name = authentication.getName();
      final String access = jwt.generateAccessToken(name);
      final String refreshToken = jwt.generateAccessToken(name);

      ResponseCookie accessCookie = cookieHelper.createAccessTokenCookie(access);
      ResponseCookie refreshCookie = cookieHelper.createRefreshTokenCookie(refreshToken);

      response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
      response.setStatus(HttpStatus.OK.value());
   }
}
