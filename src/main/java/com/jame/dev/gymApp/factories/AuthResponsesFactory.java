package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.exception.InvalidJwtException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.IdentityDto;
import com.jame.dev.gymApp.model.dto.auth.SignInOkDto;
import com.jame.dev.gymApp.service.in.CustomerService;
import com.jame.dev.gymApp.shared.enums.Role;
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

   public CookieResponseDto createRefreshCookieResponseFrom(@NonNull final String token) {
      final String subject = jwtService.extractSubject(token)
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      final boolean isValid = jwtService.isValid(token, subject);
      if (!isValid) {
         throw new InvalidJwtException("Not valid Jwt.");
      }
      return generateCookieResponseFrom(subject);
   }

   public CookieResponseDto generateCookieResponseFrom(@NonNull final String subject) {
      final String accessToken = jwtService.generateAccessToken(subject);
      final String refreshToken = jwtService.generateRefreshToken(subject);

      final ResponseCookie access = cookieHelper.createAccessTokenCookie(accessToken);
      final ResponseCookie refresh = cookieHelper.createRefreshTokenCookie(refreshToken);
      return new CookieResponseDto(access.getValue(), refresh.getValue());
   }

   public SignInOkDto createSignInOkDtoFrom(final @NonNull User authenticatedUser) {
      final boolean isUser = authenticatedUser.getAuthorities()
              .stream()
              .map(GrantedAuthority::getAuthority)
              .noneMatch(ga -> Objects.equals(ga, "ROLE_ADMIN"));
      final String username = authenticatedUser.getUsername();
      return buildSignInOkDto(isUser, username);
   }
   public IdentityDto createIdentityDtoFrom(String username, Collection<? extends GrantedAuthority> authorities) {
      final Set<Role> roles = roleMapper.authoritiesToRoles(authorities);
      return new IdentityDto(username, roles);
   }

   private SignInOkDto buildSignInOkDto(
           final boolean isUser, final String username) {
      final Optional<CustomerEntity> optionalUser = customerService.getUserByEmail(username);
      final boolean isCustomer = optionalUser.isPresent();
      final CookieResponseDto cookies = generateCookieResponseFrom(username);
      return SignInOkDto.builder()
              .isCustomer(isCustomer)
              .isUser(isUser)
              .msg("Authentication successfully")
              .email(username)
              .access(cookies.access())
              .refresh(cookies.refresh())
              .build();
   }
}
