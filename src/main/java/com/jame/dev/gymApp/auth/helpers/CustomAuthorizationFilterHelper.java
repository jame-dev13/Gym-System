package com.jame.dev.gymApp.auth.helpers;

import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilterHelper {
   private final JwtService jwtService;
   private final UserDetailsService userDetailsService;

   public boolean isAuthDoor(final HttpServletRequest request) {
      final String uri = request.getRequestURI();
      final boolean collaterals = uri.startsWith("/favicon.ico") || uri.startsWith("/error");
      final boolean oauth = (uri.startsWith("/oauth2") || uri.startsWith("/login/oauth2"));
      return uri.startsWith("/auth") || oauth || collaterals;
   }

   public Map<String, String> extractCookiesFrom(HttpServletRequest request) throws ServletException {
      if (request.getCookies() == null) return Collections.emptyMap();

      final Cookie[] cookies = Optional.ofNullable(request.getCookies())
              .orElseThrow(() -> new ServletException("unexisting cookies."));

      return Arrays.stream(cookies)
              .collect(Collectors.toMap(
                      Cookie::getName,
                      Cookie::getValue
              )
      );
   }

   public String extractSubject(final String access) {
      return jwtService.extractSubject(access)
              .orElseThrow(() -> new ExtractClaimException("Extraction Failed, unexisting claims."));
   }

   public boolean validateAccess(final String access, final String subject) {
      return jwtService.isValid(access, subject);
   }

   public void authorizeSubject(final String subject) {
      final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null) {
         final UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
         final UsernamePasswordAuthenticationToken authenticationToken =
                 new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
         SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
   }
}
