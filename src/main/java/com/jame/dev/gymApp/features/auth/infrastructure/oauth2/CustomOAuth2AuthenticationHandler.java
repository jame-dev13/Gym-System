package com.jame.dev.gymApp.features.auth.infrastructure.oauth2;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.application.contract.JwtService;
import com.jame.dev.gymApp.features.auth.application.support.helper.CookieHelper;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

   @Value("${app.auth.redirect-url}")
   private String REDIRECT_URL;

   @Override
   @AuditLog(
      input = "#authentication",
      entityType = AuditLogEntityType.AUTHENTICATION,
      action = AuditLogAction.SIGN_IN
   )
   public void onAuthenticationSuccess(final @NonNull HttpServletRequest request,
                                       final @NonNull HttpServletResponse response,
                                       final @NonNull Authentication authentication) {
      if (Objects.isNull(authentication.getPrincipal())) {
         throw new AuthenticationNullException("No user authenticated.");
      }

      String email = null;
      Long id = null;
      if (authentication.getPrincipal() instanceof CustomOAuth2User user) {
         log.info("[Oauth2 - AuthHandler]: USER IDENTIFIED.");
         email = user.getUser().email();
         id = user.getUser().id();
      }

      if (Objects.isNull(email) || email.isBlank()) {
         throw new AuthenticationNullException("No Authentication founded.");
      }

      final String access = jwt.generateAccessToken(id, email);
      final String refreshToken = jwt.generateRefreshToken(id, email);

      final ResponseCookie accessCookie = cookieHelper.createAccessTokenCookie(access);
      final ResponseCookie refreshCookie = cookieHelper.createRefreshTokenCookie(refreshToken);

      response.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
      response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
      request.getSession(false);

      try {
         response.sendRedirect(REDIRECT_URL);
      } catch (IOException e) {
         throw new RuntimeException("Cannot send redirect.", e);
      }
   }
}
