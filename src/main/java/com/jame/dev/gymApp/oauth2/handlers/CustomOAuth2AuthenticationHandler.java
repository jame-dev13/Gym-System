package com.jame.dev.gymApp.oauth2.handlers;

import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.exception.AuthenticationNullException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationHandler implements AuthenticationSuccessHandler {

   private final JwtService jwt;
   private final CookieHelper cookieHelper;
   private static final String REDIRECT_URL = "http://localhost:5173/home";

   @Override
   public void onAuthenticationSuccess(final @NonNull HttpServletRequest request,
                                       final @NonNull HttpServletResponse response,
                                       final Authentication authentication) throws IOException, ServletException {
      log.info("[Oauth2 - AuthHandler]: HIT authentication handler.");
      if(Objects.isNull(authentication.getPrincipal())){
         throw new AuthenticationNullException("No user authenticated.");
      }

      String email = null;
      if(authentication.getPrincipal() instanceof CustomOAuth2User user){
         log.info("User: {}", user.getUser().email());
         log.info("Name: {}", user.getName());
         email = user.getUser().email();
      }

      if(Objects.isNull(email) || email.isBlank()){
         throw new AuthenticationNullException("No Authentication founded.");
      }

      final String access = jwt.generateAccessToken(email);
      final String refreshToken = jwt.generateRefreshToken(email);

      final ResponseCookie accessCookie = cookieHelper.createAccessTokenCookie(access);
      final ResponseCookie refreshCookie = cookieHelper.createRefreshTokenCookie(refreshToken);

      response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
      response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
      request.getSession(false);
      response.sendRedirect(REDIRECT_URL);
   }
}
