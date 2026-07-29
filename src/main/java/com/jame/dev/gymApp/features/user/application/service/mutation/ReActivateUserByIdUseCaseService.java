package com.jame.dev.gymApp.features.user.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.application.contract.UserUpdater;
import com.jame.dev.gymApp.features.user.application.usecases.mutation.ReActivateUserByIdUseCase;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import com.jame.dev.gymApp.features.user.infrastructure.annotations.EvictUsersOnUpdate;
import com.jame.dev.gymApp.features.user.infrastructure.annotations.PublishUserRecovered;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class ReActivateUserByIdUseCaseService implements ReActivateUserByIdUseCase {
   private final UserMutationRepository userMutationRepository;
   private final UserQueryRepository userQueryRepository;
   private final UserUpdater userUpdater;

   @Override
   @Transactional
   @EvictUsersOnUpdate
   @PublishUserRecovered
   @AuditLog(
      action = AuditLogAction.RECOVER,
      entityType = AuditLogEntityType.USER,
      entityId = "#id"
   )
   public void reActivateById(long id) {
      final UserEntity user = userQueryRepository.findDeactivatedById(id)
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      final var inputWrapper = UserRequest.builder()
         .name(user.getName())
         .email(user.getEmail())
         .password(user.getPassword())
         .authProvider(user.getProvider())
         .roles(Set.of(Role.USER))
         .build();

      userUpdater.apply(user, inputWrapper);
      user.setActive(true);
      userMutationRepository.save(user);
   }
}
