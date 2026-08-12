package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.application.service.IdentityExtractorApplicationService;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
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
import org.springframework.security.core.Authentication;

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
    private IdentityExtractorApplicationService identityExtractorApplicationService;

    @Mock
    private CustomerFactory customerFactory;

    @InjectMocks
    private CreateCurrentCustomerUseCaseService service;

    @Captor
    private ArgumentCaptor<CustomerFactoryDtoInput> factoryInputCaptor;

    @Captor
    private ArgumentCaptor<CustomerEntity> customerEntityCaptor;

    private final Authentication authentication = mock(Authentication.class);
    private final CustomerCurrentRequest request = new CustomerCurrentRequest("13075523");

    @Test
    @DisplayName("Should create and return CustomerResponse building the dto from the authenticated email")
    void createCurrent_whenAuthenticatedUserIsValid_createsAndReturnsResponse() {
        final String authenticatedEmail = "user@mail.com";
        final UserEntity userRelated = new UserEntity();
        final CustomerEntity entity = new CustomerEntity();
        final CustomerEntity savedEntity = new CustomerEntity();
        final CustomerResponse response = mock(CustomerResponse.class);

        given(identityExtractorApplicationService.extract(any(Authentication.class))).willReturn(authenticatedEmail);
        given(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class))).willReturn(userRelated);
        given(customerFactory.createFromInput(any(CustomerFactoryDtoInput.class))).willReturn(entity);
        given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
        given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

        final CustomerResponse result = assertDoesNotThrow(
            () -> service.createCurrent(authentication, request));

        assertAll(
            () -> assertNotNull(result, "Result should not be null."),
            () -> assertSame(response, result, "Result should be the mapped CustomerResponse.")
        );

        verify(identityExtractorApplicationService).extract(authentication);
        verify(customerValidator).validateUserBeforeCreation(any(CustomerRequest.class));
        verify(customerFactory).createFromInput(factoryInputCaptor.capture());
        verify(customerMutationRepository).save(customerEntityCaptor.capture());
        verify(customerFactory).createFromEntity(any(CustomerEntity.class));

        final CustomerFactoryDtoInput factoryInput = factoryInputCaptor.getValue();
        assertAll(
            () -> assertSame(userRelated, factoryInput.userEntity(), "Factory input should reference the validated user."),
            () -> assertEquals(authenticatedEmail, factoryInput.dto().email(), "Email should be sourced from authentication."),
            () -> assertEquals(request.phoneContact(), factoryInput.dto().contact(), "Contact should keep the request phone contact.")
        );
        assertSame(entity, customerEntityCaptor.getValue());

        verifyNoMoreInteractions(customerMutationRepository, customerValidator, identityExtractorApplicationService, customerFactory);
    }

    @Test
    @DisplayName("Should throw AuthenticationNullException when no authenticated user is found")
    void createCurrent_whenAuthenticationIsNull_throwsAuthenticationNullException() {
        given(identityExtractorApplicationService.extract(any(Authentication.class)))
            .willThrow(new AuthenticationNullException("No Authenticated user were found."));

        assertThrows(AuthenticationNullException.class, () -> service.createCurrent(authentication, request));

        verify(identityExtractorApplicationService).extract(any(Authentication.class));
        verifyNoInteractions(customerValidator, customerMutationRepository, customerFactory);
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when customer already exists for the authenticated user")
    void createCurrent_whenCustomerAlreadyExists_throwsAlreadyExistsException() {
        given(identityExtractorApplicationService.extract(any(Authentication.class))).willReturn("user@mail.com");
        given(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class)))
            .willThrow(new AlreadyExistsException("Customer Already exists."));

        assertThrows(AlreadyExistsException.class, () -> service.createCurrent(authentication, request));

        verify(identityExtractorApplicationService).extract(any(Authentication.class));
        verify(customerValidator).validateUserBeforeCreation(any(CustomerRequest.class));
        verifyNoInteractions(customerMutationRepository, customerFactory);
    }

    @Test
    @DisplayName("Should throw NoActiveException when the authenticated user account is deactivated")
    void createCurrent_whenUserAccountDeactivated_throwsNoActiveException() {
        given(identityExtractorApplicationService.extract(any(Authentication.class))).willReturn("user@mail.com");
        given(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class)))
            .willThrow(new NoActiveException("This user's account is deactivated."));

        assertThrows(NoActiveException.class, () -> service.createCurrent(authentication, request));

        verify(identityExtractorApplicationService).extract(any(Authentication.class));
        verify(customerValidator).validateUserBeforeCreation(any(CustomerRequest.class));
        verifyNoInteractions(customerMutationRepository, customerFactory);
    }
}