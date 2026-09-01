package com.jame.dev.gymApp.backup.usecases.mutation;

import com.jame.dev.gymApp.features.backup.application.dto.BackupInput;
import com.jame.dev.gymApp.features.backup.application.service.CreateDatabaseBackupUseCaseService;
import com.jame.dev.gymApp.features.backup.application.support.factory.BackupFactory;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.model.BackupStatus;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupDumpExecutor;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupMutationRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupFailureListener;
import com.jame.dev.gymApp.features.backup.infrastructure.listener.BackupSuccessListener;
import com.jame.dev.gymApp.features.backup.infrastructure.path.PathService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CreateDatabaseBackupUseCaseServiceTest {

   @Mock
   private BackupDumpExecutor pgBackupRepository;

   @Mock
   private PathService pathService;

   @Mock
   private BackupMutationRepository mongoMutationBackupRepository;

   @Mock
   private BackupFactory backupFactory;

   @Captor
   private ArgumentCaptor<BackupSuccessListener> successListenerCaptor;

   @Captor
   private ArgumentCaptor<BackupFailureListener> failureListenerCaptor;

   @InjectMocks
   private CreateDatabaseBackupUseCaseService service;

   @Test
   @DisplayName("Should create backup path, save document and delegate dump execution")
   void createDatabaseBackup_success() {
      Path destinationPath = Path.of("/backups/database_20240101_120000.dump");
      var backupDocument = mock(BackupDocument.class);

      given(pathService.createBackupPath()).willReturn(destinationPath);
      given(backupFactory.createFromInput(any(BackupInput.class))).willReturn(backupDocument);
      given(mongoMutationBackupRepository.save(any(BackupDocument.class))).willReturn(backupDocument);

      service.createDatabaseBackup();

      verify(pathService).createBackupPath();
      verify(backupFactory).createFromInput(any(BackupInput.class));
      verify(mongoMutationBackupRepository).save(any(BackupDocument.class));
      verify(pgBackupRepository).createBackup(any(Path.class), any(BackupSuccessListener.class), any(BackupFailureListener.class));
      verifyNoMoreInteractions(pgBackupRepository, pathService, mongoMutationBackupRepository, backupFactory);
   }

   @Test
   @DisplayName("Should update document to SUCCESS with file size when dump succeeds")
   void createDatabaseBackup_successCallback_updatesStatus() {
      Path destinationPath = mock(Path.class);
      java.io.File file = mock(java.io.File.class);
      var backupDocument = mock(BackupDocument.class);

      given(pathService.createBackupPath()).willReturn(destinationPath);
      given(backupFactory.createFromInput(any(BackupInput.class))).willReturn(backupDocument);
      given(mongoMutationBackupRepository.save(any(BackupDocument.class))).willReturn(backupDocument);
      given(destinationPath.toFile()).willReturn(file);
      given(file.length()).willReturn(2048L);

      service.createDatabaseBackup();

      verify(pgBackupRepository).createBackup(
         any(Path.class),
         successListenerCaptor.capture(),
         any(BackupFailureListener.class));

      successListenerCaptor.getValue().onSuccess();

      verify(backupDocument).setSize(2048L);
      verify(backupDocument).setBackupStatus(BackupStatus.SUCCESS);
      verify(mongoMutationBackupRepository, times(2)).save(backupDocument);
   }

   @Test
   @DisplayName("Should update document to FAILURE when dump fails")
   void createDatabaseBackup_failureCallback_updatesStatus() {
      Path destinationPath = Path.of("/backups/database_20240101_120000.dump");
      var backupDocument = mock(BackupDocument.class);

      given(pathService.createBackupPath()).willReturn(destinationPath);
      given(backupFactory.createFromInput(any(BackupInput.class))).willReturn(backupDocument);
      given(mongoMutationBackupRepository.save(any(BackupDocument.class))).willReturn(backupDocument);

      service.createDatabaseBackup();

      verify(pgBackupRepository).createBackup(
         any(Path.class),
         any(BackupSuccessListener.class),
         failureListenerCaptor.capture());

      failureListenerCaptor.getValue().onFailure();

      verify(backupDocument).setBackupStatus(BackupStatus.FAILURE);
      verify(mongoMutationBackupRepository, times(2)).save(backupDocument);
   }
}