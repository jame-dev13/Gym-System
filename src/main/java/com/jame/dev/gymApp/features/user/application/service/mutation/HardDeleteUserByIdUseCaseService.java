package com.jame.dev.gymApp.features.user.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.user.application.usecases.mutation.HardDeleteUserByIdUseCase;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.infrastructure.annotations.CacheEvictUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HardDeleteUserByIdUseCaseService implements HardDeleteUserByIdUseCase {
   private final UserMutationRepository userMutationRepository;

   @Override
   @Transactional
   @CacheEvictUsers
   @AuditLog(
      action = AuditLogAction.HARD_DELETE,
      entityType = AuditLogEntityType.USER,
      entityId = "#id"
   )
   public void hardDeleteById(long id) {
      userMutationRepository.hardDeleteById(id);
   }
}
