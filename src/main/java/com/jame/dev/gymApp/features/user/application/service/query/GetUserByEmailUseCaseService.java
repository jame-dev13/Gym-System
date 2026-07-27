package com.jame.dev.gymApp.features.user.application.service.query;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.usecases.query.GetByEmailUserUseCase;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetUserByEmailUseCaseService implements GetByEmailUserUseCase {
   private final UserQueryRepository userQueryRepository;
   private final UserFactory userFactory;

   @Override
   @Cacheable(
      value = CacheValues.USER,
      key = "#email")
   public UserResponse getByEmail(String email) {
      return userQueryRepository.findByEmail(email)
         .map(userFactory::createFromEntity)
         .orElseThrow(() -> new UserEntityNotFoundException("User with email '%s' not found.".formatted(email)));
   }
}
