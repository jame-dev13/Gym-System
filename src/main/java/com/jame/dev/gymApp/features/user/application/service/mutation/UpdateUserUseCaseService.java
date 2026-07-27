package com.jame.dev.gymApp.features.user.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.user.api.request.UserUpdateRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.contract.UserUpdater;
import com.jame.dev.gymApp.features.user.application.usecases.mutation.UpdateUserUseCase;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import com.jame.dev.gymApp.features.user.infrastructure.annotations.CacheEvictUsers;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class UpdateUserUseCaseService implements UpdateUserUseCase {
   private final UserMutationRepository userMutationRepository;
   private final UserQueryRepository userQueryRepository;
   private final UserFactory userFactory;
   private final UserUpdater userUpdater;

   @Override
   @Transactional
   @CacheEvictUsers
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.USER,
      input = "#request",
      entityId = "#id",
      result = "#result"
   )
   public UserResponse update(long id, UserUpdateRequest request) {
      final UserEntity userEntity = userQueryRepository.findById(id)
         .orElseThrow(() -> new UserEntityNotFoundException("User Not found for id: " + id));
      userUpdater.apply(userEntity, request);
      final UserEntity userUpdated = userMutationRepository.save(userEntity);
      return userFactory.createFromEntity(userUpdated);
   }
}
