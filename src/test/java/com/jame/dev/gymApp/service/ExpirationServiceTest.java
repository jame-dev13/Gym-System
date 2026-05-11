package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.WindowTimeException;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.auth.application.service.ExpirationApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ExpirationServiceTest {

   @Mock
   private VerificationRepository verificationRepository;

   @InjectMocks
   private ExpirationApplicationService service;

   @Test
   @DisplayName("Expiration time updated.")
   void getMoreExpTime() {
      String email = "more@time.com";
      VerificationEntity verificationMock = mock(VerificationEntity.class);

      Instant expiration = Instant.now();

      given(verificationMock.getExpiration()).willReturn(expiration);

      given(verificationRepository.findByUser_Email(email))
              .willReturn(Optional.of(verificationMock));

      assertDoesNotThrow(() -> service.getMoreTimeFor(email));

      then(verificationRepository).should(times(1)).findByUser_Email(anyString());
      then(verificationRepository).should(times(1)).saveAndFlush(any());
      then(verificationRepository).shouldHaveNoMoreInteractions();
   }

   @Test
   @DisplayName("Should throw WindowTimeException.")
   void shouldThrowsWindowTimeException() {
      String email = "more@time.com";
      VerificationEntity verificationMock = mock(VerificationEntity.class);

      Instant expiration = Instant.now().plus(10, ChronoUnit.MINUTES);

      given(verificationMock.getExpiration()).willReturn(expiration);
      given(verificationRepository.findByUser_Email(email))
              .willReturn(Optional.of(verificationMock));

      assertThrows(WindowTimeException.class, () -> service.getMoreTimeFor(email));

      then(verificationRepository).should(times(1)).findByUser_Email(anyString());
      then(verificationRepository).shouldHaveNoMoreInteractions();
   }

   @Test
   @DisplayName("Should throw VerificationNotFoundException.")
   void shouldThrowsVerificationNotFoundException() {
      String email = "more@time.com";
      given(verificationRepository.findByUser_Email(email))
              .willThrow(VerificationNotFoundException.class);

      assertThrows(VerificationNotFoundException.class, () -> service.getMoreTimeFor(email));

      then(verificationRepository).should(times(1)).findByUser_Email(anyString());
      then(verificationRepository).shouldHaveNoMoreInteractions();
   }
}
