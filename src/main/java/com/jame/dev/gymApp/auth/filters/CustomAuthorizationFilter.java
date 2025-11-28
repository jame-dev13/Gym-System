package com.jame.dev.gymApp.auth.filters;

import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.shared.enums.CookieNames;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
   private final JwtService jwtService;
   private final UserDetailsService userDetailsService;

   private final String ACCESS_COOKIE = CookieNames.COOKIE_JWT_ACCESS.getValue();
   private final String REFRESH_COOKIE = CookieNames.COOKIE_JWT_REFRESH.getValue();

   @Override
   protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
      //Letting unprotected requests through.
      final boolean authDoor = isAuthDoor(request);
      if (authDoor) {
         filterChain.doFilter(request, response);
         return;
      }
      //get cookies
      final Map<String, String> cookies = getCookies(request)
              .orElseThrow(() -> new ServletException("Cookies are null."));
      final String access = cookies.get(ACCESS_COOKIE);

      final String subject = jwtService.extractSubject(access)
              .orElseThrow(() -> new ExtractClaimException("Claims are null."));

      if (jwtService.isValid(access, subject)) {
         authorizationHelper(subject);
         filterChain.doFilter(request, response);
      }

      final String refresh = cookies.get(REFRESH_COOKIE);
      if (jwtService.isValid(refresh, subject)) {
         authorizationHelper(subject);
         filterChain.doFilter(request, response);
      }

      filterChain.doFilter(request, response);
   }

   private static boolean isAuthDoor(HttpServletRequest request) {
      final String requestUri = request.getRequestURI();
     return (requestUri.contains("/auth")) ||
              (requestUri.contains("/refresh")) ||
              (requestUri.contains("/oauth")) ||
              (requestUri.contains("/login"));
   }

   private Optional<Map<String, String>> getCookies(HttpServletRequest request) {
      final Map<String, String> cookies = Arrays.stream(request.getCookies())
              .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
      return Optional.of(cookies);
   }

   private void authorizationHelper(final String subject) {
      final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null) {
         final UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
         UsernamePasswordAuthenticationToken authenticationToken =
                 new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
         SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
   }
}
