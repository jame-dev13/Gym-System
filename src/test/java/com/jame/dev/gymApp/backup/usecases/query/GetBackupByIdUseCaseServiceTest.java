package com.jame.dev.gymApp.backup.usecases.query;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.application.service.GetBackupByIdUseCaseService;
import com.jame.dev.gymApp.features.backup.application.support.mapper.BackupMapper;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GetBackupByIdUseCaseServiceTest {

   @Mock
   private BackupQueryRepository backupQueryRepository;

   @Mock
   private BackupMapper backupMapper;

   @InjectMocks
   private GetBackupByIdUseCaseService service;

   private final UUID backupId = UUID.randomUUID();

   @Test
   @DisplayName("Should return BackupResponse when backup document exists")
   void getById_returnsBackupResponse() {
      var document = mock(BackupDocument.class);
      var response = mock(BackupResponse.class);

      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.of(document));
      given(backupMapper.toResponse(any(BackupDocument.class))).willReturn(response);

      BackupResponse result = service.getById(backupId);

      assertSame(response, result);
      verify(backupQueryRepository).findById(any(UUID.class));
      verify(backupMapper).toResponse(any(BackupDocument.class));
      verifyNoMoreInteractions(backupQueryRepository, backupMapper);
   }

   @Test
   @DisplayName("Should throw NotFoundException when backup document does not exist")
   void getById_throwsNotFoundException() {
      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> service.getById(backupId));

      verify(backupQueryRepository).findById(any(UUID.class));
      verifyNoInteractions(backupMapper);
   }

   @Test
   @DisplayName("Should map the found document through the BackupMapper")
   void getById_mapsEntityToResponse() {
      var document = mock(BackupDocument.class);
      var response = mock(BackupResponse.class);

      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.of(document));
      given(backupMapper.toResponse(document)).willReturn(response);

      BackupResponse result = service.getById(backupId);

      assertSame(response, result);
      verify(backupMapper).toResponse(document);
   }
}