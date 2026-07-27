package com.jame.dev.gymApp.features.user.application.service.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.usecases.query.GetPageUserUseCase;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import com.jame.dev.gymApp.features.user.infrastructure.specification.UserSpecifications;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetPageUserUseCaseService implements GetPageUserUseCase {
   private final UserQueryRepository userQueryRepository;
   private final UserFactory userFactory;
   private final SortPropertyResolver userSortApplicationResolver;

   @Override
   @Cacheable(
      value = CacheValues.USERS,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<UserResponse> getPage(Pageable pageable, String search) {
      final Pageable pageableWrapped = userSortApplicationResolver.resolve(pageable);
      final Specification<UserEntity> spec = new UserSpecifications(search);
      final Page<UserEntity> entityPage = userQueryRepository.findAll(pageableWrapped, spec);
      return userFactory.createPageFrom(entityPage);
   }

   @Override
   @Cacheable(
      value = CacheValues.USERS_INACTIVE,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<UserMinimalInfoResponse> getInactivePage(Pageable pageable, String search) {
      final Pageable pageableWrapped = userSortApplicationResolver.resolve(pageable);
      final Page<UserMinimalInfoResponse> entityPage = userQueryRepository.findAllDeactivated(pageableWrapped, search);
      return userFactory.createMinimalInfoPage(entityPage);
   }
}
