package com.jame.dev.gymApp.backup.usecases.mutation;

import com.jame.dev.gymApp.features.backup.application.service.DeleteBackupByIdUseCaseService;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupMutationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteBackupByIdUseCaseServiceTest {

   @Mock
   private BackupMutationRepository backupMutationRepository;

   @Captor
   private ArgumentCaptor<UUID> uuidCaptor;

   @InjectMocks
   private DeleteBackupByIdUseCaseService service;

   private final UUID backupId = UUID.randomUUID();

   @Test
   @DisplayName("Should delete backup by the given id")
   void deleteById_success() {
      service.deleteById(backupId);

      verify(backupMutationRepository).deleteBackupById(backupId);
      verifyNoMoreInteractions(backupMutationRepository);
   }

   @Test
   @DisplayName("Should pass the exact backup id to the repository")
   void deleteById_passesExactUuid() {
      service.deleteById(backupId);

      verify(backupMutationRepository).deleteBackupById(uuidCaptor.capture());
      assertEquals(backupId, uuidCaptor.getValue());
   }
}