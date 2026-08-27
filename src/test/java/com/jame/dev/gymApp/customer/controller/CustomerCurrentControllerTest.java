package com.jame.dev.gymApp.customer.controller;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.customer.api.CustomerCurrentController;
import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.DeleteCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetCurrentCustomerUseCase;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = CustomerCurrentController.class,
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
class CustomerCurrentControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private CustomerCurrentController controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private CreateCurrentCustomerUseCase create;

   @MockitoBean
   private GetCurrentCustomerUseCase currentCustomerUseCase;

   @MockitoBean
   private UpdateCurrentCustomerUseCase update;

   @MockitoBean
   private DeleteCurrentCustomerUseCase deleteCurrent;

   private final String URI_TEMPLATE = "/app/v1/customers";
   private final CustomerResponse customerResponse = CustomerResponse.builder()
      .id(1L)
      .customerName("dto")
      .customerEmail("dto@mail")
      .contact("25082525")
      .isSubscriber(false)
      .build();

   @Nested
   @DisplayName("GET Customer Resources.")
   class CustomerCurrentControllerGetResourceTests {

      @Test
      @DisplayName("GET[200]: Get Current: /app/v1/customers/current")
      void getCurrent() throws Exception {
         given(currentCustomerUseCase.getCurrent(any()))
            .willReturn(customerResponse);
         mockMvc.perform(get(URI_TEMPLATE + "/current")
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").value(customerResponse.id()));
         verify(currentCustomerUseCase, atLeastOnce()).getCurrent(any());
         verifyNoMoreInteractions(currentCustomerUseCase);
      }

      @Test
      @DisplayName("GET[404]: Not Found: No customer related to the authenticated user")
      void getCurrentNotFound() throws Exception {
         given(currentCustomerUseCase.getCurrent(any())).willThrow(NotFoundException.class);
         mockMvc.perform(get(URI_TEMPLATE + "/current")
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("NOT_FOUND_OPERATION"));
         verify(currentCustomerUseCase, times(1)).getCurrent(any());
      }

      @Test
      @DisplayName("GET[401]: Unauthorized: No authenticated user in session")
      void getCurrentUnauthenticated() throws Exception {
         given(currentCustomerUseCase.getCurrent(any())).willThrow(AuthenticationNullException.class);
         mockMvc.perform(get(URI_TEMPLATE + "/current")
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_OPERATION"));
         verify(currentCustomerUseCase, times(1)).getCurrent(any());
      }
   }

   @Nested
   @DisplayName("POST Customer Resources.")
   class CustomerCurrentControllerPostResourceTests {

      private final String payload = """
         {
            "phoneContact": "13075523"
         }
         """;

      @Test
      @DisplayName("POST[201] Created: Register current customer: /app/v1/customers/current")
      void register() throws Exception {
         given(create.createCurrent(any(), any(CustomerCurrentRequest.class))).willReturn(customerResponse);
         mockMvc.perform(post(URI_TEMPLATE + "/current")
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").isNotEmpty());
         verify(create, times(1)).createCurrent(any(), any(CustomerCurrentRequest.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Customer already exists")
      void alreadyExists() throws Exception {
         given(create.createCurrent(any(), any(CustomerCurrentRequest.class))).willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE + "/current")
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         verify(create, times(1)).createCurrent(any(), any(CustomerCurrentRequest.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Customer is deactivated")
      void customerIsDeactivated() throws Exception {
         given(create.createCurrent(any(), any(CustomerCurrentRequest.class))).willThrow(NoActiveException.class);
         mockMvc.perform(post(URI_TEMPLATE + "/current")
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verify(create, times(1)).createCurrent(any(), any(CustomerCurrentRequest.class));
      }

      @Test
      @DisplayName("POST[401]: Unauthorized: No authenticated user in session")
      void unauthenticated() throws Exception {
         given(create.createCurrent(any(), any(CustomerCurrentRequest.class))).willThrow(AuthenticationNullException.class);
         mockMvc.perform(post(URI_TEMPLATE + "/current")
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_OPERATION"));
         verify(create, times(1)).createCurrent(any(), any(CustomerCurrentRequest.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.PHONE_CONTACT_VALIDATIONS_ERRORS,
         nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("POST[400]: Bad Request: Invalid values inside payload.")
      void badRequestInvalidPayloadValues(String phone, String codeExpected) throws Exception {
         String payload;
         if (phone == null) {
            payload = """
               {
                  "phoneContact": null
               }
               """;
         } else if ("MISSING".equals(phone)) {
            payload = "{}";
         } else {
            payload = """
               {
                  "phoneContact": "%s"
               }
               """.formatted(phone);
         }
         mockMvc.perform(post(URI_TEMPLATE + "/current")
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
         verifyNoInteractions(create);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         nullValues = "NULL",
         emptyValue = "EMPTY",
         textBlock = TestDataSource.BODY_FORMAT_ERRORS)
      @DisplayName("POST[400]: Bad Request: Invalid payload")
      void badRequestInvalidPayload(String value, String codeExpected) throws Exception {
         mockMvc.perform(
               post(URI_TEMPLATE + "/current")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(value)
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
         verifyNoInteractions(create);
      }
   }

   @Nested
   @DisplayName("PUT Customer Resources.")
   class CustomerCurrentControllerPutResources {
      String payload = """
         {
          "phoneContact": "292134525"
         }
         """;

      @Test
      @DisplayName("PUT[200] OK: Editing current customer info contact.")
      void updateInfoContact() throws Exception {
         given(update.updateCurrent(any(), any(CustomerCurrentRequest.class))).willReturn(customerResponse);
         mockMvc.perform(put(URI_TEMPLATE + "/current")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(update, times(1)).updateCurrent(any(), any(CustomerCurrentRequest.class));
      }

      @Test
      @DisplayName("PUT[404] Not Found: No customer related to the authenticated user")
      void customerNotFound() throws Exception {
         given(update.updateCurrent(any(), any(CustomerCurrentRequest.class))).willThrow(NotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE + "/current")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("NOT_FOUND_OPERATION"));
         verify(update, times(1)).updateCurrent(any(), any(CustomerCurrentRequest.class));
      }

      @Test
      @DisplayName("PUT[401]: Unauthorized: No authenticated user in session")
      void unauthenticated() throws Exception {
         given(update.updateCurrent(any(), any(CustomerCurrentRequest.class))).willThrow(AuthenticationNullException.class);
         mockMvc.perform(put(URI_TEMPLATE + "/current")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_OPERATION"));
         verify(update, times(1)).updateCurrent(any(), any(CustomerCurrentRequest.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.PHONE_CONTACT_VALIDATIONS_ERRORS,
         nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("PUT[400]: Bad Request: Invalid values inside payload.")
      void badRequestInvalidPayloadValues(String phone, String codeExpected) throws Exception {
         String payload;
         if (phone == null) {
            payload = """
               {
                  "phoneContact": null
               }
               """;
         } else if ("MISSING".equals(phone)) {
            payload = "{}";
         } else {
            payload = """
               {
                  "phoneContact": "%s"
               }
               """.formatted(phone);
         }
         mockMvc.perform(put(URI_TEMPLATE + "/current")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
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
               put(URI_TEMPLATE + "/current")
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

   @Nested
   @DisplayName("DELETE Customer Resources.")
   class CustomerCurrentControllerDeleteResources {

      @Test
      @DisplayName("DELETE[204] No Content: Current Customer Deleted")
      void downRegister() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + "/current"))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());
         verify(deleteCurrent, times(1)).deleteCurrent(any());
      }

      @Test
      @DisplayName("DELETE[401]: Unauthorized: No authenticated user in session")
      void unauthenticated() throws Exception {
         doThrow(AuthenticationNullException.class).when(deleteCurrent).deleteCurrent(any());
         mockMvc.perform(delete(URI_TEMPLATE + "/current")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_OPERATION"));
         verify(deleteCurrent, times(1)).deleteCurrent(any());
      }
   }
}