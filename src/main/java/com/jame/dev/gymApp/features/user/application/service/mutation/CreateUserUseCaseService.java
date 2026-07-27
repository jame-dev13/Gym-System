package com.jame.dev.gymApp.features.user.application.service.mutation;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.usecases.mutation.CreateUserUseCase;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserValidationRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CreateUserUseCaseService implements CreateUserUseCase {
   private final UserMutationRepository userMutationRepository;
   private final UserValidationRepository userValidationRepository;
   private final UserFactory userFactory;

   @Override
   @Transactional
   @CacheEvict(
      value = CacheValues.USERS,
      allEntries = true
   )
   @AuditLog(
      action = AuditLogAction.INSERT,
      entityType = AuditLogEntityType.USER,
      input = "#request",
      entityId = "#result.id",
      result = "#result"
   )
   public UserResponse create(UserRequest request) {
      final String email = request.email();

      if (userValidationRepository.existsAndIsDeactivatedByEmail(email)){
         throw new NoActiveException("Account linked with '%s' is unactive.".formatted(email));
      }

      if(userValidationRepository.existsByEmail(email)){
         throw new AlreadyExistsException("There's an existing account linked to '%s'.".formatted(email));
      }

      final UserEntity user = userFactory.createFromInput(request);
      final UserEntity userEntity = userMutationRepository.save(user);
      return userFactory.createFromEntity(userEntity);
   }
}
