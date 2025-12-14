package com.jame.dev.gymApp.oauth2.handlers;

import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.exception.AuthenticationNullException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.oauth2.model.AuthenticatedUser;
import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

   @Override
   public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {

      log.info("[Oauth2 - AuthHandler]: HIT authentication handler.");
      if(Objects.isNull(authentication.getPrincipal())){
         throw new AuthenticationNullException("No user authenticated.");
      }
      AuthenticatedUser authenticatedUser = null;
      if(authentication.getPrincipal() instanceof CustomOAuth2User user){
         authenticatedUser = user.getUser();
         System.out.println(authenticatedUser.roles());
         System.out.println(user.getAuthorities());
      }
      final String name = (authenticatedUser != null) ?
              authenticatedUser.email() :  authentication.getName();
      final String access = jwt.generateAccessToken(name);
      final String refreshToken = jwt.generateRefreshToken(name);

      final ResponseCookie accessCookie = cookieHelper.createAccessTokenCookie(access);
      final ResponseCookie refreshCookie = cookieHelper.createRefreshTokenCookie(refreshToken);

      response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
      response.setStatus(HttpStatus.OK.value());
      request.getSession(false);
   }
}
