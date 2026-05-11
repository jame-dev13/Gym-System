package com.jame.dev.gymApp.features.auth.application.support.factory;

import com.jame.dev.gymApp.infrastructure.config.web.CookieHelper;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.InvalidJwtException;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotFoundException;
import com.jame.dev.gymApp.features.auth.application.contract.JwtService;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import com.jame.dev.gymApp.features.auth.api.response.SessionResponse;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerService;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthResponsesFactory {
   private final JwtService jwtService;
   private final CookieHelper cookieHelper;
   private final RoleMapper roleMapper;
   private final CustomerService customerService;

   public CookieResponse createRefreshCookieResponseFrom(@NonNull final String token) {
      final String subject = jwtService.extractSubject(token)
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      final boolean isValid = jwtService.isValid(token, subject);
      if (!isValid) {
         throw new InvalidJwtException("Not valid Jwt.");
      }
      return generateCookieResponseFrom(subject);
   }

   public CookieResponse generateCookieResponseFrom(@NonNull final String subject) {
      final String accessToken = jwtService.generateAccessToken(subject);
      final String refreshToken = jwtService.generateRefreshToken(subject);

      final ResponseCookie access = cookieHelper.createAccessTokenCookie(accessToken);
      final ResponseCookie refresh = cookieHelper.createRefreshTokenCookie(refreshToken);
      return new CookieResponse(access.getValue(), refresh.getValue());
   }

   public SignInResponse createSignInOkDtoFrom(final @NonNull User authenticatedUser) {
      final boolean isUser = authenticatedUser.getAuthorities()
              .stream()
              .map(GrantedAuthority::getAuthority)
              .noneMatch(ga -> Objects.equals(ga, "ROLE_ADMIN"));
      final String username = authenticatedUser.getUsername();
      return buildSignInOkDto(isUser, username);
   }

   public SessionResponse createSessionFrom(String username, Collection<? extends GrantedAuthority> authorities) {
      final Set<Role> roles = roleMapper.authoritiesToRoles(authorities);
      final Optional<CustomerEntity> customer = customerService.getUserByEmail(username);
      return new SessionResponse(username, roles, (customer.isPresent() && customer.get().isActive()));
   }

   private SignInResponse buildSignInOkDto(
           final boolean isUser, final String username) {
      final Optional<CustomerEntity> customer = customerService.getUserByEmail(username);
      final boolean isCustomer = customer.isPresent() && customer.get().isActive();
      final CookieResponse cookies = generateCookieResponseFrom(username);
      return SignInResponse.builder()
              .isCustomer(isCustomer)
              .isUser(isUser)
              .msg("Authentication successfully")
              .email(username)
              .access(cookies.access())
              .refresh(cookies.refresh())
              .build();
   }
}
