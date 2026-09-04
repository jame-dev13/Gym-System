package com.jame.dev.gymApp.customer.controller;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.customer.api.CustomerCurrentControllerV2;
import com.jame.dev.gymApp.features.customer.api.request.CustomerUpdateRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.presentation.exception.ApiErrorResponseFactory;
import com.jame.dev.gymApp.presentation.exception.GlobalExceptionHandler;
import config.TestConfig;
import config.TestDataSource;
import config.TestValidationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = CustomerCurrentControllerV2.class,
   excludeFilters = {
      @ComponentScan.Filter(
         type = FilterType.ASSIGNABLE_TYPE,
         classes = CustomAuthorizationFilter.class
      )}
)
@AutoConfigureMockMvc(addFilters = false)
@Import({
   GlobalExceptionHandler.class,
   TestValidationConfig.class,
   TestConfig.class})
@ImportAutoConfiguration({ValidationAutoConfiguration.class})
class CustomerCurrentControllerV2Test {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private CustomerCurrentControllerV2 controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private CreateCurrentCustomerUseCase create;

   @MockitoBean
   private UpdateCurrentCustomerUseCase update;

   private final String URI_TEMPLATE = "/app/v2/customers/current";
   private final CustomerResponse customerResponse = CustomerResponse.builder()
      .id(1L)
      .customerName("dto")
      .customerEmail("dto@mail")
      .contact("25082525")
      .isSubscriber(false)
      .build();

   @Nested
   @DisplayName("POST Customer Resources.")
   class CustomerCurrentControllerV2PostResourceTests {

      @Test
      @DisplayName("POST[201] Created: /app/v2/customers/current")
      void postCustomer() throws Exception {
         given(create.createCurrent(any())).willReturn(customerResponse);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").value(customerResponse.id()));
         verify(create, times(1)).createCurrent(any());
      }

      @Test
      @DisplayName("POST[409]: Conflict: Customer already exists")
      void customerAlreadyExists() throws Exception {
         given(create.createCurrent(any())).willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         verify(create, times(1)).createCurrent(any());
      }

      @Test
      @DisplayName("POST[409]: Conflict: Customer is deactivated")
      void customerIsDeactivated() throws Exception {
         given(create.createCurrent(any())).willThrow(NoActiveException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verify(create, times(1)).createCurrent(any());
      }
   }

   @Nested
   @DisplayName("PUT Customer Resources.")
   class CustomerCurrentControllerV2PutResources {
      String payload = """
         {
          "phoneContact": "292134525",
          "addressInfo": {
             "city": "Quito",
             "locality": "Norte",
             "street": "Av. Amazonas",
             "colony": "Jipijapa",
             "homeNumber": "123",
             "cp": "170102"
          }
         }
         """;

      @Test
      @DisplayName("PUT[200] OK: Editing current customer info.")
      void putCustomer() throws Exception {
         given(update.updateCurrent(any(), any(CustomerUpdateRequest.class))).willReturn(customerResponse);
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").value(customerResponse.id()));
         verify(update, times(1)).updateCurrent(any(), any(CustomerUpdateRequest.class));
      }

      @Test
      @DisplayName("PUT[404] Not Found")
      void customerNotFound() throws Exception {
         given(update.updateCurrent(any(), any(CustomerUpdateRequest.class)))
            .willThrow(CustomerNotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));
         verify(update, times(1)).updateCurrent(any(), any(CustomerUpdateRequest.class));
      }

      @Test
      @DisplayName("PUT[400]: Bad Request: Empty phone contact.")
      void badRequestEmptyPhoneContact() throws Exception {
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content("""
                  {
                    "phoneContact": "",
                    "addressInfo": {
                       "city": "Quito"
                    }
                  }
                  """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verifyNoInteractions(update);
      }

      @Test
      @DisplayName("PUT[400]: Bad Request: Non-numeric phone contact.")
      void badRequestNonNumericPhoneContact() throws Exception {
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content("""
                  {
                    "phoneContact": "abc123",
                    "addressInfo": {
                       "city": "Quito"
                    }
                  }
                  """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verifyNoInteractions(update);
      }

      @Test
      @DisplayName("PUT[400]: Bad Request: Missing address info.")
      void badRequestMissingAddressInfo() throws Exception {
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content("""
                  {
                    "phoneContact": "292134525"
                  }
                  """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verifyNoInteractions(update);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         nullValues = "NULL",
         emptyValue = "EMPTY",
         textBlock = TestDataSource.BODY_FORMAT_ERRORS)
      @DisplayName("PUT[400]: Bad Request: Invalid payload")
      void badRequestInvalidPayload(String value, String codeExpected) throws Exception {
         mockMvc.perform(
               put(URI_TEMPLATE)
                  .accept(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(value)
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
         verifyNoInteractions(update);
      }
   }
}