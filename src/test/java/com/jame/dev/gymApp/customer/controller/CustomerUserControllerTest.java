package com.jame.dev.gymApp.customer.controller;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.customer.api.CustomerUserController;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.SoftDeleteCustomerByIdUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetByEmailCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetByIdCustomerUseCase;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = CustomerUserController.class,
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
class CustomerUserControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private CustomerUserController controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private CreateCustomerUseCase create;

   @MockitoBean
   private GetByIdCustomerUseCase getById;

   @MockitoBean
   private GetByEmailCustomerUseCase getByEmail;

   @MockitoBean
   private UpdateCustomerUseCase update;

   @MockitoBean
   private SoftDeleteCustomerByIdUseCase softDelete;

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
   class CustomerUserControllerGetResourceTests {

      @Test
      @DisplayName("GET[200] OK: get customer /customers/{id}")
      void getCurrent() throws Exception {
         given(getById.getById(anyLong())).willReturn(customerResponse);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(getById, times(1)).getById(anyLong());
      }

      @Test
      @DisplayName("GET[404] Not Found: get customer /customers/{id}")
      void customerNotFound() throws Exception {
         given(getById.getById(anyLong())).willThrow(CustomerNotFoundException.class);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 100L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         verify(getById, times(1)).getById(anyLong());
         verifyNoMoreInteractions(getById);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("GET[400] Bad Request: get customer /customers/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(getById);
      }

      @Test
      @DisplayName("GET[200] OK: get customer by email /customers/user/{email}")
      void getCurrentByEmail() throws Exception {
         given(getByEmail.getByEmail(anyString())).willReturn(customerResponse);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/user/user@mail.com")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(getByEmail, times(1)).getByEmail(anyString());
      }

      @Test
      @DisplayName("GET[404] Not Found: get customer by email /customers/user/{email}")
      void customerNotFoundByEmail() throws Exception {
         given(getByEmail.getByEmail(anyString())).willThrow(CustomerNotFoundException.class);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/user/missing@mail.com")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         verify(getByEmail, times(1)).getByEmail(anyString());
         verifyNoMoreInteractions(getByEmail);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         nullValues = "NULL",
         textBlock = """
            EMAIL,              ERROR_CODE
            not-an-email,       CONSTRAINT_OPERATION
            EMPTY,              CONSTRAINT_OPERATION
            NULL,               CONSTRAINT_OPERATION
            """)
      @DisplayName("GET[400] Bad Request: get customer by email /customers/user/{invalidEmail}")
      void badRequestInvalidEmailPathVariable(String email, String expectedCode) throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/user/" + email)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(getByEmail);
      }
   }

   @Nested
   @DisplayName("POST Customer Resources.")
   class CustomerUserControllerPostResourceTests {

      private final String payload = """
         {
            "userEmail": "user@mail.com",
            "contact": "13075523"
         }
         """;

      @Test
      @DisplayName("POST[201] Created")
      void register() throws Exception {
         given(create.create(any(CustomerRequest.class))).willReturn(customerResponse);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").isNotEmpty());
         verify(create, times(1)).create(any(CustomerRequest.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Customer already exists")
      void alreadyExists() throws Exception {
         given(create.create(any(CustomerRequest.class))).willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         verify(create, times(1)).create(any(CustomerRequest.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Customer is deactivated")
      void customerIsDeactivated() throws Exception {
         given(create.create(any(CustomerRequest.class))).willThrow(NoActiveException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verify(create, times(1)).create(any(CustomerRequest.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.PAYLOAD_VALIDATIONS_ERRORS,
         nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("POST[400]: Bad Request: Invalid values inside payload.")
      void badRequestInvalidPayloadValues(String email, String codeExpected) throws Exception {
         String payload = """
            {
               "userEmail": "%s",
               "contact": ""
            }
            """.formatted(email);
         mockMvc.perform(post(URI_TEMPLATE)
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
               post(URI_TEMPLATE)
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
   class CustomerUserControllerPutResources {
      String payload = """
         {
          "userEmail": "user@mail.com",
          "contact": "292134525"
         }
         """;

      @Test
      @DisplayName("PUT[200] OK: Editing customer info contact.")
      void updateInfoContact() throws Exception {
         given(update.update(anyLong(), any(CustomerRequest.class))).willReturn(customerResponse);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(update, times(1)).update(anyLong(), any(CustomerRequest.class));
      }

      @Test
      @DisplayName("PUT[404] Not Found")
      void customerNotFound() throws Exception {
         given(update.update(anyLong(), any(CustomerRequest.class))).willThrow(CustomerNotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));
         verify(update, times(1)).update(anyLong(), any(CustomerRequest.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("PUT[400] Bad Request: invalid id format")
      void invalidId(String value, String expectedCode) throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.put(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(update);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.PAYLOAD_VALIDATIONS_ERRORS,
         nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("PUT[400]: Bad Request: Invalid values inside payload.")
      void badRequestInvalidPayloadValues(String email, String codeExpected) throws Exception {
         String payload = """
            {
               "userEmail": "%s",
               "contact": ""
            }
            """.formatted(email);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
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
               put(URI_TEMPLATE + '/' + 1L)
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
   class CustomerUserControllerDeleteResources {

      @Test
      @DisplayName("DELETE[204] No Content: Customer Deleted")
      void downRegister() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + 1L))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());
         verify(softDelete, times(1)).softDeleteById(anyLong());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("DELETE[400] Bad Request: delete customer /customers/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(softDelete);
      }
   }
}
