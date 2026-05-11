package com.jame.dev.gymApp.features.auth.infrastructure.security;

import com.jame.dev.gymApp.features.auth.application.support.helper.CustomAuthorizationFilterHelper;
import com.jame.dev.gymApp.infrastructure.cache.BlacklistService;
import com.jame.dev.gymApp.features.auth.domain.exception.AccessExpiredException;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.domain.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.features.auth.application.contract.RateLimiterService;
import com.jame.dev.gymApp.application.contract.TryCatchBlockExecutorService;
import com.jame.dev.gymApp.features.auth.application.model.CookieNames;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
                                   @NonNull FilterChain filterChain) {

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
