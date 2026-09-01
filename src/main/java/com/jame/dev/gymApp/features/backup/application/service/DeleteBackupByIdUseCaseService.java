package com.jame.dev.gymApp.features.backup.application.service;


import com.jame.dev.gymApp.features.backup.application.usecases.DeleteBackupByIdUseCase;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupMutationRepository;
import com.jame.dev.gymApp.features.backup.infrastructure.annotations.EvictBackups;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteBackupByIdUseCaseService implements DeleteBackupByIdUseCase {
   private final BackupMutationRepository backupMutationRepository;

   @Override
   @EvictBackups
   public void deleteById(UUID uuid) {
      backupMutationRepository.deleteBackupById(uuid);
   }
}
