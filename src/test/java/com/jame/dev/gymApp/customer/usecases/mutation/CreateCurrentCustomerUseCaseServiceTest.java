package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.dto.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.features.customer.application.service.mutation.CreateCurrentCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.application.support.validator.CustomerValidator;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
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
class CreateCurrentCustomerUseCaseServiceTest {

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @Mock
    private CustomerValidator customerValidator;

    @Mock
    private CustomerFactory customerFactory;

    @InjectMocks
    private CreateCurrentCustomerUseCaseService service;

    @Captor
    private ArgumentCaptor<CustomerFactoryDtoInput> factoryInputCaptor;

    @Captor
    private ArgumentCaptor<CustomerEntity> customerEntityCaptor;

    private final String authenticatedEmail = "user@mail.com";
    private final AuthPrincipal principal = UserPrincipal.builder()
        .id(1L)
        .username(authenticatedEmail)
        .build();
    private final CustomerCurrentRequest request = new CustomerCurrentRequest("13075523");

    @Test
    @DisplayName("Should create and return CustomerResponse building the dto from the authenticated user")
    void createCurrent_whenAuthenticatedUserIsValid_createsAndReturnsResponse() {
        final UserEntity userRelated = new UserEntity();
        final CustomerEntity entity = new CustomerEntity();
        final CustomerEntity savedEntity = new CustomerEntity();
        final CustomerResponse response = mock(CustomerResponse.class);

        given(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class))).willReturn(userRelated);
        given(customerFactory.createFromInput(any(CustomerFactoryDtoInput.class))).willReturn(entity);
        given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
        given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

        final CustomerResponse result = assertDoesNotThrow(
            () -> service.createCurrent(principal, request));

        assertAll(
            () -> assertNotNull(result, "Result should not be null."),
            () -> assertSame(response, result, "Result should be the mapped CustomerResponse.")
        );

        verify(customerValidator).validateUserBeforeCreation(any(CustomerRequest.class));
        verify(customerFactory).createFromInput(factoryInputCaptor.capture());
        verify(customerMutationRepository).save(customerEntityCaptor.capture());
        verify(customerFactory).createFromEntity(any(CustomerEntity.class));

        final CustomerFactoryDtoInput factoryInput = factoryInputCaptor.getValue();
        assertAll(
            () -> assertSame(userRelated, factoryInput.userEntity(), "Factory input should reference the validated user."),
            () -> assertEquals(authenticatedEmail, factoryInput.dto().email(), "Email should be sourced from the authenticated principal."),
            () -> assertEquals(request.phoneContact(), factoryInput.dto().contact(), "Contact should keep the request phone contact.")
        );
        assertSame(entity, customerEntityCaptor.getValue());

        verifyNoMoreInteractions(customerMutationRepository, customerValidator, customerFactory);
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when customer already exists for the authenticated user")
    void createCurrent_whenCustomerAlreadyExists_throwsAlreadyExistsException() {
        given(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class)))
            .willThrow(new AlreadyExistsException("Customer Already exists."));

        assertThrows(AlreadyExistsException.class, () -> service.createCurrent(principal, request));

        verify(customerValidator).validateUserBeforeCreation(any(CustomerRequest.class));
        verifyNoInteractions(customerMutationRepository, customerFactory);
    }

    @Test
    @DisplayName("Should throw NoActiveException when the authenticated user account is deactivated")
    void createCurrent_whenUserAccountDeactivated_throwsNoActiveException() {
        given(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class)))
            .willThrow(new NoActiveException("This user's account is deactivated."));

        assertThrows(NoActiveException.class, () -> service.createCurrent(principal, request));

        verify(customerValidator).validateUserBeforeCreation(any(CustomerRequest.class));
        verifyNoInteractions(customerMutationRepository, customerFactory);
    }
}
