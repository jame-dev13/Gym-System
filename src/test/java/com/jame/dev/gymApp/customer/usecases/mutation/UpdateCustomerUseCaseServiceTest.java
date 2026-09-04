package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.domain.exception.UnrelatedDataAccessException;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerUpdateRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import com.jame.dev.gymApp.features.customer.application.service.mutation.UpdateCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerAddressInfo;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerValidationRepository;
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
   private CustomerValidationRepository customerValidationRepository;

   @Mock
   private CustomerUpdater customerUpdater;

   @Mock
   private CustomerFactory customerFactory;

   @InjectMocks
   private UpdateCustomerUseCaseService service;

   @Captor
   private ArgumentCaptor<CustomerEntity> customerEntityCaptor;

   private final CustomerRequest request = new CustomerRequest("john@mail.com", "987654321");
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
   @DisplayName("Should update and return CustomerResponse when customer exists")
   void update_whenCustomerExists_updatesAndReturnsResponse() {
      var entity = new CustomerEntity();
      var savedEntity = new CustomerEntity();
      var response = mock(CustomerResponse.class);

      given(customerValidationRepository.existsByIdAndUserEmail(anyLong(), anyString()))
         .willReturn(true);
      given(customerQueryRepository.findById(anyLong())).willReturn(Optional.of(entity));
      willDoNothing().given(customerUpdater).apply(any(CustomerEntity.class), any(CustomerRequest.class));
      given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
      given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

      var result = assertDoesNotThrow(() -> service.update(1L, request));

      assertNotNull(result);
      verify(customerValidationRepository).existsByIdAndUserEmail(anyLong(), anyString());
      verify(customerQueryRepository).findById(anyLong());
      verify(customerUpdater).apply(any(CustomerEntity.class), any(CustomerRequest.class));
      verify(customerMutationRepository).save(customerEntityCaptor.capture());
      verify(customerFactory).createFromEntity(any(CustomerEntity.class));
      assertSame(entity, customerEntityCaptor.getValue());
      verifyNoMoreInteractions(customerQueryRepository, customerMutationRepository, customerUpdater, customerFactory);
   }

   @Test
   @DisplayName("Should throw UnrelatedDataAccessException")
   void update_whenNotMatchingCustomerIdAndCustomerEmail_throwsException() {
      given(customerValidationRepository.existsByIdAndUserEmail(anyLong(), anyString()))
         .willReturn(false);

      assertThrowsExactly(UnrelatedDataAccessException.class, () -> service.update(1L, request));

      verify(customerValidationRepository).existsByIdAndUserEmail(anyLong(), anyString());
      verifyNoInteractions(customerQueryRepository, customerFactory, customerMutationRepository, customerUpdater);
   }

   @Test
   @DisplayName("Should throw NotFoundException when customer not found")
   void update_whenNotFound_throwsException() {
      given(customerValidationRepository.existsByIdAndUserEmail(anyLong(), anyString()))
         .willReturn(true);
      given(customerQueryRepository.findById(anyLong())).willReturn(Optional.empty());

      assertThrowsExactly(NotFoundException.class, () -> service.update(1L, request));

      verify(customerValidationRepository).existsByIdAndUserEmail(anyLong(), anyString());
      verify(customerQueryRepository).findById(anyLong());
      verifyNoInteractions(customerUpdater, customerMutationRepository, customerFactory);
   }

   @Test
   @DisplayName("Should update and return CustomerResponse when customer exists (update request)")
   void updateWithUpdateRequest_whenCustomerExists_updatesAndReturnsResponse() {
      var entity = new CustomerEntity();
      var savedEntity = new CustomerEntity();
      var response = mock(CustomerResponse.class);

      given(customerQueryRepository.findById(anyLong())).willReturn(Optional.of(entity));
      given(customerMutationRepository.save(any(CustomerEntity.class))).willReturn(savedEntity);
      given(customerFactory.createFromEntity(any(CustomerEntity.class))).willReturn(response);

      var result = assertDoesNotThrow(() -> service.update(1L, updateRequest));

      assertNotNull(result);
      verify(customerQueryRepository).findById(anyLong());
      verify(customerMutationRepository).save(customerEntityCaptor.capture());
      verify(customerFactory).createFromEntity(any(CustomerEntity.class));
      assertSame(entity, customerEntityCaptor.getValue());
      verifyNoMoreInteractions(customerQueryRepository, customerMutationRepository, customerFactory);
   }

   @Test
   @DisplayName("Should throw CustomerNotFoundException when customer not found (update request)")
   void updateWithUpdateRequest_whenNotFound_throwsException() {
      given(customerQueryRepository.findById(anyLong())).willReturn(Optional.empty());

      assertThrowsExactly(CustomerNotFoundException.class, () -> service.update(1L, updateRequest));

      verify(customerQueryRepository).findById(anyLong());
      verifyNoInteractions(customerMutationRepository, customerFactory);
   }
}
