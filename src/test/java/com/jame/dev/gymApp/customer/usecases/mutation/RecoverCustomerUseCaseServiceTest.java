package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.service.mutation.RecoverCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RecoverCustomerUseCaseServiceTest {

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @Mock
   private CustomerFactory customerFactory;

    @InjectMocks
    private RecoverCustomerUseCaseService service;

    @Test
    @DisplayName("Should call repository recoverByUserEmail")
    void recover_callsRepositoryRecoverByUserEmail() {
        var request = new RecoveryRequest("test@mail.com");

        service.recover(request);

        verify(customerMutationRepository).recoverByUserEmail(request.email());
        verify(customerFactory).createFromEntity(any());
        verifyNoMoreInteractions(customerMutationRepository, customerFactory);
    }
}
