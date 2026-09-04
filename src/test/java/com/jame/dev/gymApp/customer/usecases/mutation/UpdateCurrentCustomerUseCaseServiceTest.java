package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerUpdateRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import com.jame.dev.gymApp.features.customer.application.service.mutation.UpdateCurrentCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerAddressInfo;
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
class UpdateCurrentCustomerUseCaseServiceTest {

    @Mock
    private CustomerQueryRepository customerQueryRepository;

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @Mock
    private CustomerUpdater customerUpdater;

    @Mock
    private CustomerFactory customerFactory;

    @InjectMocks
    private UpdateCurrentCustomerUseCaseService service;

    @Captor
    private ArgumentCaptor<CustomerEntity> customerEntityCaptor;

    @Captor
    private ArgumentCaptor<CustomerRequest> appliedRequestCaptor;

    private final String authenticatedEmail = "user@mail.com";
    private final AuthPrincipal principal = UserPrincipal.builder()
        .id(1L)
        .username(authenticatedEmail)
        .build();
    private final CustomerCurrentRequest request = new CustomerCurrentRequest("292134525");
    private final CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
        "987654321",
        CustomerAddressInfo.builder()
            .city("Quito")
            .locality("Norte")
            .street("Av. Amazonas")
            .colony("Jipijapa")
            .homeNumber("123")
            .cp("170102")
            .build());

    @Test
    @DisplayName("Should update and return CustomerResponse building the dto from the authenticated user")
    void updateCurrent_whenCustomerExists_updatesAndReturnsResponse() {
        final CustomerEntity entity = new CustomerEntity();
        final CustomerEntity savedEntity = new CustomerEntity();
        final CustomerResponse response = mock(CustomerResponse.class);

        given(customerQueryRepository.findByUserEmail(authenticatedEmail)).willReturn(Optional.of(entity));
        willDoNothing().given(customerUpdater).apply(any(CustomerEntity.class), any(CustomerRequest.class));
        given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
        given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

        final CustomerResponse result = assertDoesNotThrow(
            () -> service.updateCurrent(principal, request));

        assertAll(
            () -> assertNotNull(result, "Result should not be null."),
            () -> assertSame(response, result, "Result should be the mapped CustomerResponse.")
        );

        verify(customerQueryRepository).findByUserEmail(authenticatedEmail);
        verify(customerUpdater).apply(same(entity), appliedRequestCaptor.capture());
        verify(customerMutationRepository).save(customerEntityCaptor.capture());
        verify(customerFactory).createFromEntity(any(CustomerEntity.class));

        final CustomerRequest applied = appliedRequestCaptor.getValue();
        assertAll(
            () -> assertEquals(authenticatedEmail, applied.email(), "Email should be sourced from the authenticated principal."),
            () -> assertEquals(request.phoneContact(), applied.contact(), "Contact should keep the request phone contact.")
        );
        assertSame(entity, customerEntityCaptor.getValue());

        verifyNoMoreInteractions(customerQueryRepository, customerMutationRepository, customerUpdater, customerFactory);
    }

    @Test
    @DisplayName("Should throw NotFoundException when no customer is related to the authenticated user")
    void updateCurrent_whenCustomerNotFound_throwsNotFoundException() {
        given(customerQueryRepository.findByUserEmail(authenticatedEmail)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateCurrent(principal, request));

        verify(customerQueryRepository).findByUserEmail(authenticatedEmail);
        verifyNoInteractions(customerUpdater, customerMutationRepository, customerFactory);
    }

    @Test
    @DisplayName("Should update and return CustomerResponse building the update request from the authenticated principal")
    void updateCurrentWithUpdateRequest_whenCustomerExists_updatesAndReturnsResponse() {
        final CustomerEntity entity = new CustomerEntity();
        final CustomerEntity savedEntity = new CustomerEntity();
        final CustomerResponse response = mock(CustomerResponse.class);

        given(customerQueryRepository.findById(anyLong())).willReturn(Optional.of(entity));
        given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
        given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

        final CustomerResponse result = assertDoesNotThrow(
            () -> service.updateCurrent(principal, updateRequest));

        assertAll(
            () -> assertNotNull(result, "Result should not be null."),
            () -> assertSame(response, result, "Result should be the mapped CustomerResponse.")
        );

        verify(customerQueryRepository).findById(anyLong());
        verify(customerMutationRepository).save(customerEntityCaptor.capture());
        verify(customerFactory).createFromEntity(any(CustomerEntity.class));
        assertSame(entity, customerEntityCaptor.getValue());

        verifyNoMoreInteractions(customerQueryRepository, customerMutationRepository, customerFactory);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when no customer is related to the authenticated principal")
    void updateCurrentWithUpdateRequest_whenCustomerNotFound_throwsCustomerNotFoundException() {
        given(customerQueryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrowsExactly(CustomerNotFoundException.class,
            () -> service.updateCurrent(principal, updateRequest));

        verify(customerQueryRepository).findById(anyLong());
        verifyNoInteractions(customerMutationRepository, customerFactory);
    }
}
