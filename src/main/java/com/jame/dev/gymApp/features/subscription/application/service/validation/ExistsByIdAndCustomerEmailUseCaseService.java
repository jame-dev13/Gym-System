package com.jame.dev.gymApp.features.subscription.application.service.validation;

import com.jame.dev.gymApp.features.subscription.application.usecases.validation.ExistsByIdAndCustomerEmailUseCase;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class ExistsByIdAndCustomerEmailUseCaseService implements ExistsByIdAndCustomerEmailUseCase {
    private final SubscriptionValidationRepository subscriptionValidationRepository;

    @Override
    public boolean existsByIdAndCustomerEmail(long id, String email) {
        return subscriptionValidationRepository.existsByIdAndCustomerEmail(id, email);
    }
}
