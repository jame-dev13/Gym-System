package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.customer.application.service.mutation.SoftDeleteCustomerByIdUseCaseService;
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
class SoftDeleteCustomerByIdUseCaseServiceTest {

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @InjectMocks
    private SoftDeleteCustomerByIdUseCaseService service;

    @Test
    @DisplayName("Should call repository deleteById")
    void softDeleteById_callsRepositoryDeleteById() {
        service.softDeleteById(1L);

        verify(customerMutationRepository).deleteById(anyLong());
        verifyNoMoreInteractions(customerMutationRepository);
    }
}
