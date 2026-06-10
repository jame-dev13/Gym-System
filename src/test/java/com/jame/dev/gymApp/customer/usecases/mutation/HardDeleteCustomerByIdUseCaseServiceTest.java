package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.customer.application.service.mutation.HardDeleteCustomerByIdUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class HardDeleteCustomerByIdUseCaseServiceTest {

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @InjectMocks
    private HardDeleteCustomerByIdUseCaseService service;

    @Test
    @DisplayName("Should call repository hardDeleteById")
    void hardDeleteById_callsRepositoryHardDeleteById() {
        service.hardDeleteById(1L);

        verify(customerMutationRepository).hardDeleteById(anyLong());
        verifyNoMoreInteractions(customerMutationRepository);
    }
}
