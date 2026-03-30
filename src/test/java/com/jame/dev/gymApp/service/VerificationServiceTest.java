package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.factories.VerificationFactory;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.out.VerificationServiceImplementation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationServiceTest {
   @Mock
   UserRepository userRepository;
   @Mock
   PasswordEncoder passwordEncoder;
   @Mock
   VerificationRepository verificationRepository;
   @Mock
   VerificationFactory verificationFactory;

   @InjectMocks
   VerificationServiceImplementation service;

   private final VerificationEntity verificationEntity = new VerificationEntity();
   private final UserEntity user = new UserEntity();
   private final VerificationDto verificationDto = new VerificationDto(
           OffsetDateTime.now(), "mail@verified.com", true, "verified"
   );

   @Test
   @DisplayName("Save VerificationEntity")
   void save() {
      given(userRepository.findById(anyLong()))
              .willReturn(Optional.of(user));
      given(passwordEncoder.encode(anyString())).willReturn("tokenHashed");
      given(verificationRepository.saveAndFlush(any()))
              .willReturn(verificationEntity);

      final var result = service.save(1L, "rawToken");

      assertNotNull(result);

      then(userRepository).should(times(1)).findById(anyLong());
      then(passwordEncoder).should(times(1)).encode(anyString());
      then(verificationRepository).should(times(1)).saveAndFlush(any());

      verifyNoMoreInteractions(userRepository, passwordEncoder, verificationRepository);
   }

   @Test
   @DisplayName("Success verification")
   void verify() {
      String email = "mail@verified.com";
      VerificationEntity verification = mock(VerificationEntity.class);

      given(verification.getToken()).willReturn("tokenHashed");
      given(verification.getExpiration()).willReturn(Instant.now().plus(10, ChronoUnit.MINUTES));

      given(verificationRepository.findByUser_Email(email))
              .willReturn(Optional.of(verification));
      given(passwordEncoder.matches(anyString(), any()))
              .willReturn(true);
      given(verificationRepository.saveAndFlush(any(VerificationEntity.class)))
              .willReturn(verification);

      assertDoesNotThrow(() -> service.verify(email, "rawToken"));

      then(verificationRepository).should().findByUser_Email(anyString());
      then(passwordEncoder).should().matches(anyString(), any());
      then(verificationRepository).should().saveAndFlush(any(VerificationEntity.class));
      verifyNoInteractions(verificationFactory);
      verifyNoMoreInteractions(userRepository, verificationRepository);
   }

   @Test
   @DisplayName("Verification attempt failed")
   void verifyFails() {
      VerificationEntity verification = mock(VerificationEntity.class);

      given(verification.getToken()).willReturn("tokenHashed");
      given(verification.getExpiration()).willReturn(Instant.now().plus(10, ChronoUnit.MINUTES));
      given(verificationRepository.findByUser_Email(anyString()))
              .willReturn(Optional.of(verification));
      given(passwordEncoder.matches(anyString(), anyString()))
              .willReturn(false);
      assertThrowsExactly(
              VerificationAttemptFailedException.class,
              () -> service.verify("email@mail.com", "rawToken")
      );

      then(verificationRepository).should(times(1)).findByUser_Email(anyString());
      then(passwordEncoder).should(times(1)).matches(anyString(), anyString());
      verifyNoMoreInteractions(verificationRepository, passwordEncoder);
      verifyNoInteractions(userRepository, verificationFactory);
   }

   @Test
   @DisplayName("Is Verified")
   void isVerified() {
      given(verificationRepository.existsByUser_EmailAndVerifiedTrue(anyString()))
              .willReturn(true);

      assertTrue(service.isVerified("verified@verfied.com"));

      then(verificationRepository).should(times(1))
              .existsByUser_EmailAndVerifiedTrue(anyString());
      verifyNoMoreInteractions(verificationRepository);
      verifyNoInteractions(userRepository, passwordEncoder, verificationFactory);
   }

//      service.delete(token);
//
//      verify(verificationRepository).findById(token);
//      verify(verificationRepository).deleteById(token);
//   }

//   @Test
//   @DisplayName("Not remove verified records")
//   void notRemoveVerified() {
//      final String token = verificationEntity.getId();
//      verificationEntity.setVerified(true);
//
//      when(verificationRepository.findById(token))
//              .thenReturn(Optional.of(verificationEntity));
//      service.delete(token);
//
//      assertTrue(verificationEntity.isVerified(), "Should be verified.");
//
//      verify(verificationRepository).findById(token);
//      verify(verificationRepository, never()).deleteById(token);
//   }
}
