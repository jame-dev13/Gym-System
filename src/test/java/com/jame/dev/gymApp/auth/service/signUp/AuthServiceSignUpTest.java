package com.jame.dev.gymApp.auth.service.signUp;

import com.jame.dev.gymApp.auth.service.AuthServiceImplementation;
import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.AuthProviderNotAllowedException;
import com.jame.dev.gymApp.factories.AuthResponsesFactory;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceSignUpTest {

   @Mock
   private UserService userService;
   @Mock
   private AuthenticationManager authenticationManager;
   @Mock
   private BlacklistService blacklistService;
   @Mock
   private VerificationService verificationService;
   @Mock
   private EmailService emailService;
   @Mock
   private AuthResponsesFactory authFactory;

   @InjectMocks
   private AuthServiceImplementation service;

   private UserDtoInput mockInput = new UserDtoInput(
           "mock",
           "mock@mail.com",
           "mock1234",
           AuthProvider.LOCAL,
           Set.of(Role.USER
           ));

   private final UserDtoOutput user = new UserDtoOutput(
           1L,
           "mockUser",
           "mock@mail.com",
           Set.of(Role.USER)
   );

   @Test
   @DisplayName("SingUp successfully made.")
   public void signUp() {
      VerificationDto verification = new VerificationDto(
              OffsetDateTime.now(),
              "mock@mail.com",
              true,
              "verified."
      );

      given(mockInput.authProvider()).willReturn(AuthProvider.LOCAL);
      given(userService.save(any(UserDtoInput.class))).willReturn(user);
      given(verificationService.save(anyLong())).willReturn(new VerificationEntity());
      given(verificationService.verify(anyString(), anyString())).willReturn(verification);
      given(emailService.sendSimpleEmail(any(EmailDetails.class)))
              .willReturn(CompletableFuture.completedFuture(true));

      service.signUp(mockInput);

      then(userService).should(times(1)).save(mockInput);
      then(verificationService).should(times(1)).save(anyLong());
      then(verificationService).should(times(1)).verify(anyString(), anyString());
      then(emailService).should(times(1)).sendSimpleEmail(any(EmailDetails.class));
      verifyNoMoreInteractions(userService, verificationService, emailService);
   }

   @Test
   @DisplayName("SignUp: Throws AuthProviderNotAllowedException")
   public void shouldThrowsAuthProviderNotAllowedException() {
      UserDtoInput input = mock(UserDtoInput.class);

      given(input.authProvider()).willReturn(AuthProvider.GOOGLE);

      assertThrows(AuthProviderNotAllowedException.class, () -> service.signUp(input));

      verifyNoInteractions(userService, verificationService, emailService);
   }

}
