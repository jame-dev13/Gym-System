package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.auth.application.service.IdentityExtractorApplicationService;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import com.jame.dev.gymApp.features.customer.application.service.mutation.UpdateCurrentCustomerUseCaseService;
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
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCurrentCustomerUseCaseServiceTest {

    @Mock
    private CustomerQueryRepository customerQueryRepository;

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @Mock
    private CustomerUpdater customerUpdater;

    @Mock
    private IdentityExtractorApplicationService identityExtractorApplicationService;

    @Mock
    private CustomerFactory customerFactory;

    @InjectMocks
    private UpdateCurrentCustomerUseCaseService service;

    @Captor
    private ArgumentCaptor<CustomerEntity> customerEntityCaptor;

    @Captor
    private ArgumentCaptor<CustomerRequest> appliedRequestCaptor;

    private final Authentication authentication = mock(Authentication.class);
    private final CustomerCurrentRequest request = new CustomerCurrentRequest("292134525");

    @Test
    @DisplayName("Should update and return CustomerResponse building the dto from the authenticated email")
    void updateCurrent_whenCustomerExists_updatesAndReturnsResponse() {
        final String authenticatedEmail = "user@mail.com";
        final CustomerEntity entity = new CustomerEntity();
        final CustomerEntity savedEntity = new CustomerEntity();
        final CustomerResponse response = mock(CustomerResponse.class);

        given(identityExtractorApplicationService.extract(any(Authentication.class))).willReturn(authenticatedEmail);
        given(customerQueryRepository.findByUserEmail(authenticatedEmail)).willReturn(Optional.of(entity));
        willDoNothing().given(customerUpdater).apply(any(CustomerEntity.class), any(CustomerRequest.class));
        given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
        given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

        final CustomerResponse result = assertDoesNotThrow(
            () -> service.updateCurrent(authentication, request));

        assertAll(
            () -> assertNotNull(result, "Result should not be null."),
            () -> assertSame(response, result, "Result should be the mapped CustomerResponse.")
        );

        verify(identityExtractorApplicationService).extract(authentication);
        verify(customerQueryRepository).findByUserEmail(authenticatedEmail);
        verify(customerUpdater).apply(same(entity), appliedRequestCaptor.capture());
        verify(customerMutationRepository).save(customerEntityCaptor.capture());
        verify(customerFactory).createFromEntity(any(CustomerEntity.class));

        final CustomerRequest applied = appliedRequestCaptor.getValue();
        assertAll(
            () -> assertEquals(authenticatedEmail, applied.email(), "Email should be sourced from authentication."),
            () -> assertEquals(request.phoneContact(), applied.contact(), "Contact should keep the request phone contact.")
        );
        assertSame(entity, customerEntityCaptor.getValue());

        verifyNoMoreInteractions(customerQueryRepository, customerMutationRepository, customerUpdater, identityExtractorApplicationService, customerFactory);
    }

    @Test
    @DisplayName("Should throw NotFoundException when no customer is related to the authenticated user")
    void updateCurrent_whenCustomerNotFound_throwsNotFoundException() {
        final String authenticatedEmail = "user@mail.com";

        given(identityExtractorApplicationService.extract(any(Authentication.class))).willReturn(authenticatedEmail);
        given(customerQueryRepository.findByUserEmail(authenticatedEmail)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateCurrent(authentication, request));

        verify(identityExtractorApplicationService).extract(any(Authentication.class));
        verify(customerQueryRepository).findByUserEmail(authenticatedEmail);
        verifyNoInteractions(customerUpdater, customerMutationRepository, customerFactory);
    }

    @Test
    @DisplayName("Should throw AuthenticationNullException when no authenticated user is found")
    void updateCurrent_whenAuthenticationIsNull_throwsAuthenticationNullException() {
        given(identityExtractorApplicationService.extract(any(Authentication.class)))
            .willThrow(new AuthenticationNullException("No Authenticated user were found."));

        assertThrows(AuthenticationNullException.class, () -> service.updateCurrent(authentication, request));

        verify(identityExtractorApplicationService).extract(any(Authentication.class));
        verifyNoInteractions(customerQueryRepository, customerUpdater, customerMutationRepository, customerFactory);
    }
}