package com.jame.dev.gymApp.backup.usecases.query;

import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.backup.application.service.GetBackupDumpFileUseCaseService;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.path.PathService;
import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GetBackupDumpFileUseCaseServiceTest {

   @Mock
   private BackupQueryRepository backupQueryRepository;

   @Mock
   private PathService pathService;

   @Mock
   private HashExecutor hashExecutor;

   @InjectMocks
   private GetBackupDumpFileUseCaseService service;

   private final UUID backupId = UUID.randomUUID();

   @Test
   @DisplayName("Should return FileSystemResource when document exists and checksum matches")
   void getResourceDumpFile_returnsResource() {
      var document = mock(BackupDocument.class);
      String fileName = "database_20240101_120000.dump";
      Path filePath = Path.of("/backups", fileName);

      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.of(document));
      given(document.getFileName()).willReturn(fileName);
      given(document.getChecksum()).willReturn("checksum");
      given(hashExecutor.verify(anyString(), anyString())).willReturn(true);
      given(pathService.resolveBackupFilePath(anyString())).willReturn(filePath);

      Resource result = service.getResourceDumpFile(backupId);

      assertNotNull(result);
      assertSame(FileSystemResource.class, result.getClass());
      verify(backupQueryRepository).findById(any(UUID.class));
      verify(document).getFileName();
      verify(hashExecutor).verify(anyString(), anyString());
      verify(pathService).resolveBackupFilePath(anyString());
      verifyNoMoreInteractions(backupQueryRepository, pathService, hashExecutor);
   }

   @Test
   @DisplayName("Should throw NotFoundException when document does not exist")
   void getResourceDumpFile_throwsNotFoundException() {
      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> service.getResourceDumpFile(backupId));

      verify(backupQueryRepository).findById(any(UUID.class));
      verifyNoInteractions(pathService, hashExecutor);
   }

   @Test
   @DisplayName("Should throw MissMatchException when checksum does not match")
   void getResourceDumpFile_throwsMissMatchException() {
      var document = mock(BackupDocument.class);
      String fileName = "database_20240101_120000.dump";

      given(backupQueryRepository.findById(any(UUID.class))).willReturn(Optional.of(document));
      given(document.getFileName()).willReturn(fileName);
      given(document.getChecksum()).willReturn("checksum");
      given(hashExecutor.verify(anyString(), anyString())).willReturn(false);

      assertThrows(MissMatchException.class, () -> service.getResourceDumpFile(backupId));

      verify(backupQueryRepository).findById(any(UUID.class));
      verify(hashExecutor).verify(anyString(), anyString());
      verifyNoMoreInteractions(backupQueryRepository, hashExecutor);
   }
}