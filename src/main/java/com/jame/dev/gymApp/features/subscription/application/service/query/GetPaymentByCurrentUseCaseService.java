package com.jame.dev.gymApp.features.subscription.application.service.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.subscription.api.response.PaymentResponse;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetPaymentByCurrent;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.payment.mapper.PaymentMapper;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class GetPaymentByCurrentUseCaseService implements GetPaymentByCurrent {
   private final PaymentQueryRepository paymentQueryRepository;
   private final PaymentMapper paymentMapper;
   private final SortPropertyResolver paymentSortResolver;
   private final IdentityExtractorService identityExtractorService;

   @Override
   @Cacheable(
      value = CacheValues.PAYMENTS,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<PaymentResponse> getPaymentByCurrent(Authentication authentication, String search, Pageable pageable) {
      final Pageable pageableWrapped = paymentSortResolver.resolve(pageable);
      final String authName = identityExtractorService.extract(authentication);
      final Page<PaymentEntity> entityPage = paymentQueryRepository.findPaymentPage(authName, search, pageableWrapped);
      final var content = entityPage.getContent().stream()
         .map(paymentMapper::toResponse)
         .toList();
      return new PageDto<>(
         content,
         entityPage.getNumber(),
         entityPage.getSize(),
         entityPage.getTotalElements(),
         entityPage.getSort().toString(),
         entityPage.getSort().isSorted() ? "ASC" : "DESC");
   }
}
