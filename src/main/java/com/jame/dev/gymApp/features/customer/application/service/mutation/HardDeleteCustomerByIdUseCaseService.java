package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.features.customer.application.usecases.mutation.HardDeleteCustomerByIdUseCase;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.annotations.EvictOnDropCustomers;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class HardDeleteCustomerByIdUseCaseService implements HardDeleteCustomerByIdUseCase {
   private final CustomerMutationRepository customerMutationRepository;

   @Override
   @Transactional
   @EvictOnDropCustomers
   public void hardDeleteById(long id) {
      customerMutationRepository.hardDeleteById(id);
   }
}
