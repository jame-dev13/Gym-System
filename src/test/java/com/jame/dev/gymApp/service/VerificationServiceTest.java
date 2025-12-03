package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import com.jame.dev.gymApp.service.out.VerificationServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {
   @Mock
   private VerificationRepository verificationRepository;

   @Mock
   private TokenGeneratorService tokenGeneratorService;

   @InjectMocks
   private VerificationServiceImplementation service;

   private final VerificationEntity verificationEntity =
           VerificationEntity.builder()
                   .id("ABC123")
                   .user(new UserEntity())
                   .expiration(Instant.now().plus(10, ChronoUnit.MINUTES))
                   .verified(false)
                   .build();

  @Test
  @DisplayName("Save VerificationEntity")
  void save(){
     when(tokenGeneratorService.generateToken()).thenReturn(verificationEntity.getId());
     when(verificationRepository.save(any(VerificationEntity.class)))
             .thenAnswer(invocation -> invocation.getArgument(0));
     VerificationEntity verificationAdded = service.save(new UserEntity());

     ArgumentCaptor<VerificationEntity> captor = ArgumentCaptor.forClass(VerificationEntity.class);
     verify(tokenGeneratorService).generateToken();
     verify(verificationRepository).save(captor.capture());

     VerificationEntity verificationSaved = captor.getValue();

     assertAll("Not null, objects should be equals and not verified.",
             () -> assertNotNull(verificationSaved, "Should not be null."),
             () -> assertSame(verificationAdded, verificationSaved, "Should be the same object."),
             () -> assertEquals(verificationAdded, verificationSaved, "Objects should be equals."),
             () -> assertFalse(verificationSaved.isVerified(), "Object recently saved should not be verified.")
     );
  }


   @Test
   @DisplayName("Success verification")
   void verification(){
      final String token = verificationEntity.getId();
      when(verificationRepository.findById(token))
              .thenReturn(Optional.of(verificationEntity));
      when(verificationRepository.save(verificationEntity))
              .thenReturn(verificationEntity);

      boolean verified = service.verify(token);
      verificationEntity.setVerified(true);

      assertTrue(verified, "Should have success.");
      assertTrue(verificationEntity.isVerified(), "Should be verified.");

      verify(verificationRepository).findById(token);
      verify(verificationRepository).save(verificationEntity);
   }

   @Test
   @DisplayName("Remove only unverified records")
   void removeUnverified(){
      final String token = verificationEntity.getId();
      when(verificationRepository.findById(token))
              .thenReturn(Optional.of(verificationEntity));

      assertFalse(verificationEntity.isVerified(), "Should not be verified");

      service.delete(token);

      verify(verificationRepository).findById(token);
      verify(verificationRepository).deleteById(token);
   }

   @Test
   @DisplayName("Not remove verified records")
   void notRemoveVerified(){
      final String token = verificationEntity.getId();
      verificationEntity.setVerified(true);

      when(verificationRepository.findById(token))
              .thenReturn(Optional.of(verificationEntity));
      service.delete(token);

      assertTrue(verificationEntity.isVerified(), "Should be verified.");

      verify(verificationRepository).findById(token);
      verify(verificationRepository, never()).deleteById(token);
   }

}
