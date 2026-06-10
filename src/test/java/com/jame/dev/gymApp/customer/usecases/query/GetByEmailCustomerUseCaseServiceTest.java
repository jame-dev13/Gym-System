package com.jame.dev.gymApp.customer.usecases.query;

import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.service.query.GetByEmailCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapper;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetByEmailCustomerUseCaseServiceTest {

    @Mock
    private CustomerQueryRepository customerQueryRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private GetByEmailCustomerUseCaseService service;

    @Test
    @DisplayName("Should return CustomerResponse when customer exists")
    void getByEmail_whenCustomerExists_returnsCustomerResponse() {
        var entity = new CustomerEntity();
        var response = mock(CustomerResponse.class);
        given(customerQueryRepository.findByUserEmail(anyString())).willReturn(Optional.of(entity));
        given(customerMapper.toDto(any(CustomerEntity.class))).willReturn(response);

        var result = service.getByEmail("test@mail.com");

        assertNotNull(result);
        verify(customerQueryRepository).findByUserEmail(anyString());
        verify(customerMapper).toDto(any(CustomerEntity.class));
        verifyNoMoreInteractions(customerQueryRepository, customerMapper);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found")
    void getByEmail_whenCustomerNotFound_throwsException() {
        given(customerQueryRepository.findByUserEmail(anyString())).willReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.getByEmail("test@mail.com"));

        verify(customerQueryRepository).findByUserEmail(anyString());
        verifyNoInteractions(customerMapper);
    }
}
