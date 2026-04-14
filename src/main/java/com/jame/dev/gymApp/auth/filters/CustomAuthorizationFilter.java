package com.jame.dev.gymApp.auth.filters;

import com.jame.dev.gymApp.auth.helpers.CustomAuthorizationFilterHelper;
import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.exception.AccessExpiredException;
import com.jame.dev.gymApp.exception.AuthenticationNullException;
import com.jame.dev.gymApp.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.service.in.RateLimiterService;
import com.jame.dev.gymApp.service.in.TryCatchBlockExecutorService;
import com.jame.dev.gymApp.shared.enums.CookieNames;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
   private final BlacklistService blacklistService;
   private final CustomAuthorizationFilterHelper authorizationHelper;
   private final RateLimiterService rateLimiterService;
   private final TryCatchBlockExecutorService blockExecutorService;

   private static final String REFRESH_COOKIE = CookieNames.COOKIE_JWT_REFRESH.getValue();
   private static final String ACCESS_COOKIE = CookieNames.COOKIE_JWT_ACCESS.getValue();

   @Override
   protected void doFilterInternal(HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain) throws ServletException, IOException {

      log.info(request.getRequestURI());
      if (authorizationHelper.isAuthDoor(request)) {
         blockExecutorService.executeVoidBlock(request, response, () -> {
            rateLimiterService.fixedWindow(request);
            filterChain.doFilter(request, response);
         });
         return;
      }

      blockExecutorService.executeVoidBlock(request, response, () -> {
         final Map<String, String> cookies = authorizationHelper.extractCookiesFrom(request);
         if (cookies.isEmpty()) {
            throw new AuthenticationNullException("There's no authentication.");
         }

         final String access = cookies.get(ACCESS_COOKIE);
         if (access == null) {
            throw new AccessExpiredException("Access expired.");
         }

         final String subject = authorizationHelper.extractSubject(access);

         if (authorizationHelper.validateAccess(access, subject)) {
            authorizationHelper.authorizeSubject(subject);
            filterChain.doFilter(request, response);
            return;
         }

         final String refresh = cookies.get(REFRESH_COOKIE);

         if (blacklistService.isBlacklisted(refresh)) {
            throw new TokenAlreadyBlacklistedException("Token already blacklisted.");
         }

         if (authorizationHelper.validateAccess(refresh, subject)) {
            authorizationHelper.authorizeSubject(subject);
            filterChain.doFilter(request, response);
            return;
         }
         filterChain.doFilter(request, response);
      });
   }
}
