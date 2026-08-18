package com.jame.dev.gymApp.customer.usecases.query;

import com.jame.dev.gymApp.features.auth.application.service.IdentityExtractorApplicationService;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
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
import org.springframework.security.core.Authentication;

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

   @Mock
   private IdentityExtractorApplicationService identityExtractorApplicationService;

   @InjectMocks
   private GetCurrentCustomerUseCaseService service;

   @Test
   @DisplayName("Get Current CustomerResponse")
   void getCustomerResponse_CurrentByAuthentication() {
      final String subjectAuth = "any@mail.com";
      final CustomerEntity customer = mock(CustomerEntity.class);
      final CustomerResponse response = mock(CustomerResponse.class);
      final Authentication auth = mock(Authentication.class);

      given(identityExtractorApplicationService.extract(any(Authentication.class)))
         .willReturn(subjectAuth);
      given(customerQueryRepository.findByUserEmail(anyString()))
         .willReturn(Optional.of(customer));
      given(customerFactory.createFromEntity(any(CustomerEntity.class)))
         .willReturn(response);

      final CustomerResponse result = assertDoesNotThrow(
         () -> service.getCurrent(auth), "Should return the current authenticated related customer.");

      assertAll(
         () -> assertNotNull(result, "Result should not be null."),
         () -> assertSame(CustomerResponse.class, result.getClass(), "Result Type should be CustomerResponse.class.")
      );

      verify(identityExtractorApplicationService, atLeastOnce()).extract(any((Authentication.class)));
      verify(customerQueryRepository, atLeastOnce()).findByUserEmail(anyString());
      verify(customerFactory, atLeastOnce()).createFromEntity(any(CustomerEntity.class));
      verifyNoMoreInteractions(customerQueryRepository, customerFactory, identityExtractorApplicationService);
   }

   @Test
   @DisplayName("Get Current Customer - Not Found")
   void getCustomerResponse_CurrentByAuthentication_whenCustomerAbsent_throwsNotFoundException() {
      final String subjectAuth = "any@mail.com";
      final Authentication auth = mock(Authentication.class);

      given(identityExtractorApplicationService.extract(any(Authentication.class)))
         .willReturn(subjectAuth);
      given(customerQueryRepository.findByUserEmail(subjectAuth))
         .willReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> service.getCurrent(auth));

      verify(identityExtractorApplicationService).extract(any(Authentication.class));
      verify(customerQueryRepository).findByUserEmail(subjectAuth);
      verifyNoInteractions(customerFactory);
   }

   @Test
   @DisplayName("Get Current Customer - Unauthenticated")
   void getCustomerResponse_CurrentByAuthentication_whenAuthenticationAbsent_throwsAuthenticationNullException() {
      final Authentication auth = mock(Authentication.class);

      given(identityExtractorApplicationService.extract(any(Authentication.class)))
         .willThrow(new AuthenticationNullException("No Authenticated user were found."));

      assertThrows(AuthenticationNullException.class, () -> service.getCurrent(auth));

      verify(identityExtractorApplicationService).extract(any(Authentication.class));
      verifyNoInteractions(customerQueryRepository, customerFactory);
   }

}
