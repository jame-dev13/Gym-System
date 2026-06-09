package com.jame.dev.gymApp.features.user.application.service.query;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.usecases.query.GetByIdUserUseCase;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserByIdUseCaseService implements GetByIdUserUseCase {
   private final UserQueryRepository userQueryRepository;
   private final UserFactory userFactory;

   @Override
   @Cacheable(
      value = CacheValues.USER,
      key = "#id"
   )
   public UserResponse getById(Long id) {
      return userQueryRepository.findById(id)
         .map(userFactory::createFromEntity)
         .orElseThrow(() -> new UserEntityNotFoundException("User with: '%d' not found.".formatted(id)));
   }
}
