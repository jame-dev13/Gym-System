package com.jame.dev.gymApp.customer.usecases.query;

import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.service.query.GetByIdCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class GetByIdCustomerUseCaseServiceTest {

    @Mock
    private CustomerQueryRepository customerQueryRepository;

    @Mock
    private CustomerFactory customerFactory;

    @InjectMocks
    private GetByIdCustomerUseCaseService service;

    @Test
    @DisplayName("Should return CustomerResponse when customer exists")
    void getById_whenCustomerExists_returnsCustomerResponse() {
        var entity = new CustomerEntity();
        var response = mock(CustomerResponse.class);
        given(customerQueryRepository.findById(anyLong())).willReturn(Optional.of(entity));
        given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

        var result = service.getById(1L);

        assertNotNull(result);
        verify(customerQueryRepository).findById(anyLong());
        verify(customerFactory).createFromEntity(any(CustomerEntity.class));
        verifyNoMoreInteractions(customerQueryRepository, customerFactory);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found")
    void getById_whenCustomerNotFound_throwsException() {
        given(customerQueryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.getById(1L));

        verify(customerQueryRepository).findById(anyLong());
        verifyNoInteractions(customerFactory);
    }
}
