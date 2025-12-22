package com.jame.dev.gymApp.auth.filters;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.exception.AccessExpiredException;
import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.shared.enums.CookieNames;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
   private final JwtService jwtService;
   private final UserDetailsService userDetailsService;
   private final BlacklistService blacklistService;
   private final AuthenticationEntryPoint authenticationEntryPoint;

   private final String ACCESS_COOKIE = CookieNames.COOKIE_JWT_ACCESS.getValue();
   private final String REFRESH_COOKIE = CookieNames.COOKIE_JWT_REFRESH.getValue();

   @Override
   protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

      log.info(request.getRequestURI());
      final boolean authDoor = isAuthDoor(request);
      if (authDoor) {
         log.info("[FILTER] skipping: {}", request.getRequestURI());
         filterChain.doFilter(request, response);
         return;
      }

      if (request.getCookies() == null) {
         return;
      }

      final Map<String, String> cookies = getCookies(request);

      final String access = cookies.get(ACCESS_COOKIE);
      if (access == null) {
         authenticationEntryPoint.commence(request, response, new AccessExpiredException("Access expired."));
      }
      final String subject = jwtService.extractSubject(access)
              .orElseThrow(() -> new ExtractClaimException("Claims are null."));

      if (jwtService.isValid(access, subject)) {
         authorizationHelper(subject);
         filterChain.doFilter(request, response);
         return;
      }

      final String refresh = cookies.get(REFRESH_COOKIE);
      if (blacklistService.isBlacklisted(refresh)) {
         authenticationEntryPoint.commence(request, response, new TokenAlreadyBlacklistedException("Token already blacklisted."));
      }

      if (jwtService.isValid(refresh, subject)) {
         authorizationHelper(subject);
         filterChain.doFilter(request, response);
         return;
      }
      filterChain.doFilter(request, response);
   }

   private boolean isAuthDoor(HttpServletRequest request) {
      final String uri = request.getRequestURI();
      final boolean collaterals = uri.startsWith("/favicon.ico") || uri.startsWith("/error");
      final boolean oauth = (uri.startsWith("/oauth2") || uri.startsWith("/login/oauth2"));
      return uri.startsWith("/auth") || oauth || collaterals;
   }

   private Map<String, String> getCookies(HttpServletRequest request) throws ServletException {
      final Cookie[] cookies = Optional.ofNullable(request.getCookies())
              .orElseThrow(() -> new ServletException("No cookies present."));
      return Arrays.stream(cookies)
              .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
   }

   private void authorizationHelper(final String subject) {
      final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null) {
         final UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
         final UsernamePasswordAuthenticationToken authenticationToken =
                 new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
         SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
   }
}
