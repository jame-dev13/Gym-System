package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import com.jame.dev.gymApp.features.customer.application.service.mutation.UpdateCustomerUseCaseService;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerUseCaseServiceTest {

    @Mock
    private CustomerQueryRepository customerQueryRepository;

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @Mock
    private CustomerUpdater customerUpdater;

    @Mock
    private CustomerFactory customerFactory;

    @InjectMocks
    private UpdateCustomerUseCaseService service;

    @Captor
    private ArgumentCaptor<CustomerEntity> customerEntityCaptor;

    private final CustomerRequest request = new CustomerRequest("john@mail.com", "987654321");

    @Test
    @DisplayName("Should update and return CustomerResponse when customer exists")
    void update_whenCustomerExists_updatesAndReturnsResponse() {
        var entity = new CustomerEntity();
        var savedEntity = new CustomerEntity();
        var response = mock(CustomerResponse.class);

        given(customerQueryRepository.findById(anyLong())).willReturn(Optional.of(entity));
        willDoNothing().given(customerUpdater).apply(any(CustomerEntity.class), any(CustomerRequest.class));
        given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
        given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

        var result = service.update(1L, request);

        assertNotNull(result);
        verify(customerQueryRepository).findById(anyLong());
        verify(customerUpdater).apply(any(CustomerEntity.class), any(CustomerRequest.class));
        verify(customerMutationRepository).save(customerEntityCaptor.capture());
        verify(customerFactory).createFromEntity(any(CustomerEntity.class));
        assertSame(entity, customerEntityCaptor.getValue());
        verifyNoMoreInteractions(customerQueryRepository, customerMutationRepository, customerUpdater, customerFactory);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found")
    void update_whenCustomerNotFound_throwsException() {
        given(customerQueryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.update(1L, request));

        verify(customerQueryRepository).findById(anyLong());
        verifyNoInteractions(customerUpdater, customerMutationRepository, customerFactory);
    }
}
