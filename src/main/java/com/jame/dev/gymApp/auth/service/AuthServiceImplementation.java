package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.model.dto.auth.*;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.CustomerService;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImplementation implements AuthService {

   private final UserService userService;
   private final CustomerService customerService;
   private final JwtService jwtService;
   private final CookieHelper cookieHelper;
   private final AuthenticationManager authenticationManager;
   private final BlacklistService blacklistService;
   private final VerificationService verificationService;
   private final EmailService emailService;
   private final RoleMapper roleMapper;

   @Override
   public void signUp(final UserDtoInput dto) {
      if (dto.authProvider() != AuthProvider.LOCAL) {
         throw new AuthProviderNotAllowedException("Non LOCAL provider present: " + dto.authProvider());
      }
      final UserEntity user = userService.save(new UserDtoInput(
              dto.name(), dto.email(), dto.password(),
              AuthProvider.LOCAL, Set.of(Role.USER))
      );
      if (user == null) {
         throw new CantSaveUserException("Operation failed.");
      }
      final VerificationEntity verification = verificationService.save(user);
      if (verification == null) {
         throw new CantSaveVerifcationEntityException("Can't save the verification.");
      }
      final String recipient = user.getEmail();
      final EmailDetails emailDetails = EmailDetails.builder()
              .recipient(recipient)
              .subject("Verification code")
              .msgBody(emailService.html(recipient, verification.getId()))
              .build();
      emailService.sendSimpleEmail(emailDetails)
              .thenAccept(sent ->
                 log.warn("{}", (sent) ? "Mail message sent": "Error try to send the mail."));
   }

   @Override
   public SignInOkDto signIn(SignInDto dto) {
      if (!isLocalProvider(dto)) {
         throw new NonLocalAuthenticationAllowedException("This should not be authenticated by the local provider.");
      }

      if (!verificationService.isVerified(dto.email())) {
         throw new UserNotVerifiedException("%s hadn't verified his account yet.".formatted(dto.email()));
      }

      final UsernamePasswordAuthenticationToken token =
              new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
      final Authentication authentication = authenticationManager.authenticate(token);
      log.info("[Auth-Service]: Auth done.");

      final User userAuthenticated = Optional.ofNullable((User) authentication.getPrincipal())
              .orElseThrow(() -> new AuthenticationAttemptFailureException("Can't authenticate User."));

      final boolean isUser = userAuthenticated.getAuthorities()
              .stream()
              .noneMatch(ga -> Objects.equals(ga.getAuthority(), "ROLE_ADMIN"));

      final String username = userAuthenticated.getUsername();
      final Optional<CustomerEntity> optionalUser = customerService.getUserByEmail(username);
      final boolean isCustomer = optionalUser.isPresent();
      final CookieResponseDto cookies = handleCookieResponse(username);

      return SignInOkDto.builder()
              .isCustomer(isCustomer)
              .isUser(isUser)
              .msg("Authentication successfully")
              .email(username)
              .access(cookies.access())
              .refresh(cookies.refresh())
              .build();
   }

   @Override
   public CookieResponseDto refresh(String token) {
      blacklistService.blacklistToken(token);
      final String subject = jwtService.extractSubject(token)
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      final boolean isValid = jwtService.isValid(token, subject);
      if (!isValid) {
         throw new InvalidJwtException("Not valid Jwt.");
      }
      return handleCookieResponse(subject);
   }

   @Override
   public Optional<VerificationDto> verify(final String email, final String code) {
      return Optional.of(verificationService.verify(email, code));
   }

   @Override
   public ExpirationWindowDto setNewExpiration(@NonNull String email) {
      return verificationService.getMoreExpTime(email);
   }

   @Override
   public AuthMe setUser(@NonNull String value, @NonNull final Authentication authentication) {
      if (Objects.isNull(value))
         throw new AuthenticationNullException("Not valid access authentication.");
      final String username = jwtService.extractSubject(value)
              .orElseThrow(() -> new UserNotFoundException("Subject not found."));
      if (!username.equals(authentication.getName()))
         throw new IllegalSubjectAuthenticatedException("Subjects doesn't match.");
      final Set<Role> roles = roleMapper.authoritiesToRoles(authentication.getAuthorities());
      return new AuthMe(username, roles);
   }

   private boolean isLocalProvider(SignInDto dto) {
      final UserEntity entityUser = userService.getUserByEmail(dto.email())
              .orElseThrow(() -> new UserNotFoundException("No user with email: " + dto.email()));
      return entityUser.getProvider() == AuthProvider.LOCAL;
   }

   private CookieResponseDto handleCookieResponse(final String subject) {
      log.info("[Cookie-Response]: HIT cookie response.");
      final String accessToken = jwtService.generateAccessToken(subject);
      final String refreshToken = jwtService.generateRefreshToken(subject);

      final ResponseCookie access = cookieHelper.createAccessTokenCookie(accessToken);
      final ResponseCookie refresh = cookieHelper.createRefreshTokenCookie(refreshToken);
      return new CookieResponseDto(access.getValue(), refresh.getValue());
   }
}
