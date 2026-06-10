package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.dto.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.features.customer.application.service.mutation.CreateCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.application.support.validator.CustomerValidator;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseServiceTest {

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @Mock
    private CustomerValidator customerValidator;

    @Mock
    private CustomerFactory customerFactory;

    @InjectMocks
    private CreateCustomerUseCaseService service;

    @Captor
    private ArgumentCaptor<CustomerEntity> customerEntityCaptor;

    private final CustomerRequest request = new CustomerRequest("john@mail.com", "123456789");

    @Test
    @DisplayName("Should create and return CustomerResponse when validation passes")
    void create_whenValidationPasses_createsAndReturnsResponse() {
        var user = new UserEntity();
        var entity = new CustomerEntity();
        var savedEntity = new CustomerEntity();
        var response = mock(CustomerResponse.class);

        given(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class))).willReturn(user);
        given(customerFactory.createFromInput(any(CustomerFactoryDtoInput.class))).willReturn(entity);
        given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
        given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

        var result = service.create(request);

        assertNotNull(result);
        verify(customerValidator).validateUserBeforeCreation(request);
        verify(customerFactory).createFromInput(any(CustomerFactoryDtoInput.class));
        verify(customerMutationRepository).save(customerEntityCaptor.capture());
        verify(customerFactory).createFromEntity(any(CustomerEntity.class));
        assertSame(entity, customerEntityCaptor.getValue());
        verifyNoMoreInteractions(customerMutationRepository, customerValidator, customerFactory);
    }

    @Test
    @DisplayName("Should throw exception when validation fails")
    void create_whenValidationFails_throwsException() {
        given(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class)))
                .willThrow(new UserEntityNotFoundException("User not found."));

        assertThrows(UserEntityNotFoundException.class, () -> service.create(request));

        verify(customerValidator).validateUserBeforeCreation(request);
        verifyNoInteractions(customerFactory, customerMutationRepository);
    }
}
