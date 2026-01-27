package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.factories.AuthResponsesFactory;
import com.jame.dev.gymApp.factories.EmailDetailsFactory;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.messages.service.HtmlTemplates;
import com.jame.dev.gymApp.model.dto.auth.*;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
   private final JwtService jwtService;
   private final AuthenticationManager authenticationManager;
   private final BlacklistService blacklistService;
   private final VerificationService verificationService;
   private final EmailService emailService;
   private final AuthResponsesFactory authFactory;

   @Override
   public void signUp(final UserDtoInput dto) {
      if (dto.authProvider() != AuthProvider.LOCAL) {
         throw new AuthProviderNotAllowedException("Non LOCAL provider present: " + dto.authProvider());
      }
      final UserDtoInput dtoValid = new UserDtoInput(
              dto.name(), dto.email(),
              dto.password(),
              AuthProvider.LOCAL, Set.of(Role.USER)
      );
      final UserEntity user = userService.save(dtoValid);
      if (user == null) {
         throw new CantSaveUserException("Operation failed.");
      }
      final VerificationEntity verification = verificationService.save(user);
      if (verification == null) {
         throw new CantSaveVerifcationEntityException("Can't save the verification.");
      }
      final EmailDetails emailDetails = EmailDetailsFactory.createDetailsFrom(
              user.getEmail(),
              "Verification Code",
              HtmlTemplates.verificationTemplate()
                      .replace("{{recipient}}", user.getEmail())
                      .replace("{{token}}", verification.getId()));
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

      return authFactory.createSignInOkDtoFrom(userAuthenticated);
   }

   @Override
   public CookieResponseDto refresh(String token) {
      blacklistService.blacklistToken(token);
      return authFactory.createRefreshCookieResponseFrom(token);
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
   public IdentityDto setUser(@NonNull String access, @NonNull final Authentication authentication) {
      if (Objects.isNull(access))
         throw new AuthenticationNullException("Not valid access authentication.");
      final String tokenSubject = jwtService.extractSubject(access)
              .orElseThrow(() -> new UserNotFoundException("Subject not found."));

      final String subjectExpected = getIdentifierFromPrincipal(authentication);

      log.info("Token subject: [{}] with subject Identifier: [{}]", tokenSubject, subjectExpected);

      if (!tokenSubject.equals(subjectExpected))
         throw new IllegalSubjectAuthenticatedException("Subjects doesn't match.");
      return authFactory.createIdentityDtoFrom(tokenSubject, authentication.getAuthorities());
   }

   private String getIdentifierFromPrincipal(Authentication authentication) {
      if(authentication.getPrincipal() instanceof CustomOAuth2User user) {
         return user.getUser().email();
      }
      return authentication.getName();
   }

   private boolean isLocalProvider(SignInDto dto) {
      final UserEntity entityUser = userService.getUserByEmail(dto.email())
              .orElseThrow(() -> new UserNotFoundException("No user with email: " + dto.email()));
      return entityUser.getProvider() == AuthProvider.LOCAL;
   }
}
