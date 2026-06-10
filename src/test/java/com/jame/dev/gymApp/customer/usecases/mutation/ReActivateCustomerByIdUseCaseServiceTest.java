package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.customer.application.service.mutation.ReActivateCustomerByIdUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReActivateCustomerByIdUseCaseServiceTest {

    @Mock
    private CustomerQueryRepository customerQueryRepository;

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @InjectMocks
    private ReActivateCustomerByIdUseCaseService service;

    @Captor
    private ArgumentCaptor<CustomerEntity> customerEntityCaptor;

    @Test
    @DisplayName("Should reactivate customer when found")
    void reActivateById_whenCustomerFound_reactivatesAndSaves() {
        var deactivatedEntity = new CustomerEntity();
        deactivatedEntity.setActive(false);

        given(customerQueryRepository.findDeactivatedById(anyLong())).willReturn(Optional.of(deactivatedEntity));
        given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(deactivatedEntity);

        service.reActivateById(1L);

        verify(customerQueryRepository).findDeactivatedById(anyLong());
        verify(customerMutationRepository).save(customerEntityCaptor.capture());

        var saved = customerEntityCaptor.getValue();
        assertTrue(saved.isActive());
        assertSame(deactivatedEntity, saved);
        verifyNoMoreInteractions(customerQueryRepository, customerMutationRepository);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found")
    void reActivateById_whenCustomerNotFound_throwsException() {
        given(customerQueryRepository.findDeactivatedById(anyLong())).willReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.reActivateById(1L));

        verify(customerQueryRepository).findDeactivatedById(anyLong());
        verifyNoInteractions(customerMutationRepository);
    }
}
