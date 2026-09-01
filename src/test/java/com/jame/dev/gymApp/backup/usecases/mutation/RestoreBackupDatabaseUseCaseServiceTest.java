package com.jame.dev.gymApp.backup.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.backup.application.service.RestoreBackupDatabaseUseCaseService;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.model.BackupStatus;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupMutationRepository;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupRestoreExecutor;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupFailureListener;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupSuccessListener;
import com.jame.dev.gymApp.features.backup.infrastructure.path.PathService;
import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RestoreBackupDatabaseUseCaseServiceTest {

   @Mock
   private BackupQueryRepository backupQueryRepository;

   @Mock
   private BackupMutationRepository backupMutationRepository;

   @Mock
   private BackupRestoreExecutor pgBackupRestoreRepository;

   @Mock
   private HashExecutor hashExecutor;

   @Mock
   private PathService pathService;

   @Captor
   private ArgumentCaptor<BackupSuccessListener> successListenerCaptor;

   @Captor
   private ArgumentCaptor<BackupFailureListener> failureListenerCaptor;

   @InjectMocks
   private RestoreBackupDatabaseUseCaseService service;

   private final UUID backupId = UUID.randomUUID();

   private final String fileName = "database_20240101_120000.dump";

   @Test
   @DisplayName("Should restore backup when document exists and checksum matches")
   void restore_success() {
      var backupDocument = mock(BackupDocument.class);

      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.of(backupDocument));
      given(backupDocument.getFileName()).willReturn(fileName);
      given(backupDocument.getChecksum()).willReturn("checksum");
      given(hashExecutor.verify(anyString(), anyString())).willReturn(true);
      given(pathService.resolveBackupFilePath(anyString())).willReturn(Path.of("/backups", fileName));

      service.restore(backupId);

      verify(backupQueryRepository).findById(any(UUID.class));
      verify(hashExecutor).verify(anyString(), anyString());
      verify(pathService).resolveBackupFilePath(anyString());
      verify(pgBackupRestoreRepository).restore(
         any(Path.class), any(BackupSuccessListener.class), any(BackupFailureListener.class));
      verifyNoMoreInteractions(backupQueryRepository, backupMutationRepository, pgBackupRestoreRepository, hashExecutor, pathService);
   }

   @Test
   @DisplayName("Should throw NotFoundException when backup document does not exist")
   void restore_throwsNotFoundException() {
      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> service.restore(backupId));

      verify(backupQueryRepository).findById(any(UUID.class));
      verifyNoInteractions(backupMutationRepository, pgBackupRestoreRepository, hashExecutor, pathService);
   }

   @Test
   @DisplayName("Should throw MissMatchException when checksum does not match")
   void restore_throwsMissMatchException() {
      var backupDocument = mock(BackupDocument.class);

      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.of(backupDocument));
      given(backupDocument.getFileName()).willReturn(fileName);
      given(backupDocument.getChecksum()).willReturn("checksum");
      given(pathService.resolveBackupFilePath(anyString())).willReturn(Path.of("/backups", fileName));
      given(hashExecutor.verify(anyString(), anyString())).willReturn(false);

      assertThrows(MissMatchException.class, () -> service.restore(backupId));

      verify(backupQueryRepository).findById(any(UUID.class));
      verify(pathService).resolveBackupFilePath(anyString());
      verify(hashExecutor).verify(anyString(), anyString());
      verifyNoMoreInteractions(backupQueryRepository, backupMutationRepository, hashExecutor, pathService);
      verifyNoInteractions(pgBackupRestoreRepository);
   }

   @Test
   @DisplayName("Should update document to RESTORED when restore succeeds")
   void restore_successCallback_updatesStatus() {
      var backupDocument = mock(BackupDocument.class);

      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.of(backupDocument));
      given(backupDocument.getFileName()).willReturn(fileName);
      given(backupDocument.getChecksum()).willReturn("checksum");
      given(hashExecutor.verify(anyString(), anyString())).willReturn(true);
      given(pathService.resolveBackupFilePath(anyString())).willReturn(Path.of("/backups", fileName));

      service.restore(backupId);

      verify(pgBackupRestoreRepository).restore(
         any(Path.class), successListenerCaptor.capture(), any(BackupFailureListener.class));

      successListenerCaptor.getValue().onSuccess();

      verify(backupDocument).setBackupStatus(BackupStatus.RESTORED);
      verify(backupMutationRepository).save(backupDocument);
   }

   @Test
   @DisplayName("Should update document to RESTORED_FAIL when restore fails")
   void restore_failureCallback_updatesStatus() {
      var backupDocument = mock(BackupDocument.class);

      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.of(backupDocument));
      given(backupDocument.getFileName()).willReturn(fileName);
      given(backupDocument.getChecksum()).willReturn("checksum");
      given(hashExecutor.verify(anyString(), anyString())).willReturn(true);
      given(pathService.resolveBackupFilePath(anyString())).willReturn(Path.of("/backups", fileName));

      service.restore(backupId);

      verify(pgBackupRestoreRepository).restore(
         any(Path.class), any(BackupSuccessListener.class), failureListenerCaptor.capture());

      failureListenerCaptor.getValue().onFailure();

      verify(backupDocument).setBackupStatus(BackupStatus.RESTORED_FAIL);
      verify(backupMutationRepository).save(backupDocument);
   }
}