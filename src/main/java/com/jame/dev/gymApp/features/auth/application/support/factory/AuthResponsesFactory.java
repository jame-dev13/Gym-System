package com.jame.dev.gymApp.features.auth.application.support.factory;

import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import com.jame.dev.gymApp.features.auth.api.response.SessionResponse;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.features.auth.application.contract.JwtService;
import com.jame.dev.gymApp.features.auth.application.model.JwtValidationArgument;
import com.jame.dev.gymApp.features.auth.application.support.helper.CookieHelper;
import com.jame.dev.gymApp.features.auth.domain.exception.ExtractClaimException;
import com.jame.dev.gymApp.features.auth.domain.exception.InvalidJwtException;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthResponsesFactory {
   private final JwtService jwtService;
   private final CookieHelper cookieHelper;
   private final RoleMapper roleMapper;
   private final UserRepository userRepository;

   public CookieResponse createRefreshCookieResponseFrom(@NonNull final String token) {
      final long userId = jwtService.extractUserId(token)
         .orElseThrow(() -> new ExtractClaimException("Cannot extract 'userId' claim."));
      final String subject = jwtService.extractSubject(token)
         .orElseThrow(() -> new ExtractClaimException("Cannot extract 'subject' claim."));
      final boolean isValid = jwtService.isValid(new JwtValidationArgument(token, subject, userId));
      if (!isValid) {
         throw new InvalidJwtException("Not valid Jwt.");
      }
      return generateCookieResponseFrom(userId, subject);
   }

   public CookieResponse generateCookieResponseFrom(long userId, @NonNull final String subject) {
      final String accessToken = jwtService.generateAccessToken(userId, subject);
      final String refreshToken = jwtService.generateRefreshToken(userId, subject);

      final ResponseCookie access = cookieHelper.createAccessTokenCookie(accessToken);
      final ResponseCookie refresh = cookieHelper.createRefreshTokenCookie(refreshToken);
      return new CookieResponse(access.getValue(), refresh.getValue());
   }

   public SignInResponse createSignInOkDtoFrom(final @NonNull UserPrincipal authenticatedUser) {
      final boolean isUser = authenticatedUser.getAuthorities()
         .stream()
         .map(GrantedAuthority::getAuthority)
         .noneMatch(ga -> Objects.equals(ga, "ROLE_ADMIN"));
      return buildSignInOkDto(isUser, authenticatedUser);
   }

   public SessionResponse createSessionFrom(String username, Collection<? extends GrantedAuthority> authorities) {
      final Set<Role> roles = roleMapper.authoritiesToRoles(authorities);
      boolean isCustomer = userRepository.findByEmail(username)
         .map(UserEntity::getCustomerEntity)
         .isPresent();
      return new SessionResponse(username, roles, isCustomer);
   }

   @NonNull
   private SignInResponse buildSignInOkDto(
      final boolean isUser, final UserPrincipal userPrincipal) {
      final CookieResponse cookies = generateCookieResponseFrom(userPrincipal.getId(), userPrincipal.getUsername());
      return SignInResponse.builder()
         .isUser(isUser)
         .email(userPrincipal.getUsername())
         .access(cookies.access())
         .refresh(cookies.refresh())
         .build();
   }
}
