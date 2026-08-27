package com.jame.dev.gymApp.customer.usecases.query;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.service.query.GetCurrentCustomerUseCaseService;
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
public class GetCurrentCustomerUseCaseServiceTest {
   @Mock
   private CustomerQueryRepository customerQueryRepository;

   @Mock
   private CustomerFactory customerFactory;

   @InjectMocks
   private GetCurrentCustomerUseCaseService service;

   private final String subjectAuth = "any@mail.com";
   private final AuthPrincipal principal = UserPrincipal.builder()
      .id(1L)
      .username(subjectAuth)
      .build();

   @Test
   @DisplayName("Get Current CustomerResponse")
   void getCustomerResponse_CurrentByPrinciapl() {
      final CustomerEntity customer = mock(CustomerEntity.class);
      final CustomerResponse response = mock(CustomerResponse.class);

      given(customerQueryRepository.findByUserEmail(anyString()))
         .willReturn(Optional.of(customer));
      given(customerFactory.createFromEntity(any(CustomerEntity.class)))
         .willReturn(response);

      final CustomerResponse result = assertDoesNotThrow(
         () -> service.getCurrent(principal), "Should return the current authenticated related customer.");

      assertAll(
         () -> assertNotNull(result, "Result should not be null."),
         () -> assertSame(CustomerResponse.class, result.getClass(), "Result Type should be CustomerResponse.class.")
      );

      verify(customerQueryRepository, atLeastOnce()).findByUserEmail(subjectAuth);
      verify(customerFactory, atLeastOnce()).createFromEntity(any(CustomerEntity.class));
      verifyNoMoreInteractions(customerQueryRepository, customerFactory);
   }

   @Test
   @DisplayName("Get Current Customer - Not Found")
   void getCustomerResponse_CurrentByPrincipal_whenCustomerAbsent_throwsNotFoundException() {
      given(customerQueryRepository.findByUserEmail(subjectAuth))
         .willReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> service.getCurrent(principal));

      verify(customerQueryRepository).findByUserEmail(subjectAuth);
      verifyNoInteractions(customerFactory);
   }
}
