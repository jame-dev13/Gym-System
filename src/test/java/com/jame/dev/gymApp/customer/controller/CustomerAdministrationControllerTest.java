package com.jame.dev.gymApp.customer.controller;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.customer.api.CustomerAdministrationController;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.RecoverCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.SoftDeleteCustomerByIdUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetByIdCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetPageCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.domain.model.Role;
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

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = CustomerAdministrationController.class,
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
class CustomerAdministrationControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private CustomerAdministrationController controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private CreateCustomerUseCase create;

   @MockitoBean
   private GetByIdCustomerUseCase getById;

   @MockitoBean
   private GetPageCustomerUseCase getPage;

   @MockitoBean
   private RecoverCustomerUseCase recover;

   @MockitoBean
   private UpdateCustomerUseCase update;

   @MockitoBean
   private SoftDeleteCustomerByIdUseCase softDelete;

   private final String URI_TEMPLATE = "/app/v1/administration/customers";
   private final CustomerResponse customerResponse = new CustomerResponse(
      1L, new UserResponse(1L, "dto", "dto@mail", AuthProvider.LOCAL, Set.of(Role.USER)), "25082525"
   );

   @Nested
   @DisplayName("GET Customer Resources.")
   class CustomerAdministrationControllerGetResourceTests {

      @Test
      @DisplayName("GET[200] OK: get page /customers?page=0&size=1")
      void getPage() throws Exception {
         PageDto<CustomerResponse> page = mock();
         given(page.content()).willReturn(List.of());
         given(page.totalElements()).willReturn(0L);
         given(getPage.getPage(any(), any())).willReturn(page);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE)
               .param("page", "0")
               .param("size", "1")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").exists());
         verify(getPage, times(1)).getPage(any(), any());
      }

      @Test
      @DisplayName("GET[200] OK: get customer /customers/{id}")
      void getCustomer() throws Exception {
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
   }

   @Nested
   @DisplayName("POST Customer Resources.")
   class CustomerAdministrationControllerPostResourceTests {

      private final String payload = """
         {
            "userEmail": "user@mail.com",
            "contact": "13075523"
         }
         """;

      @Test
      @DisplayName("POST[201] Created")
      void postCustomer() throws Exception {
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
      void customerAlreadyExists() throws Exception {
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
   @DisplayName("POST Recover Customer Resources.")
   class CustomerAdministrationControllerPostRecoverResources {

      @Test
      @DisplayName("POST[201] Created: Customer recovered")
      void recoverCustomer() throws Exception {
         given(recover.recover(any(RecoveryRequest.class))).willReturn(customerResponse);
         mockMvc.perform(post(URI_TEMPLATE + "/recover")
               .contentType(MediaType.APPLICATION_JSON)
               .content("""
                  { "email": "user@mail.com" }
                  """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
         verify(recover, times(1)).recover(any(RecoveryRequest.class));
      }
   }

   @Nested
   @DisplayName("PUT Customer Resources.")
   class CustomerAdministrationControllerPutResources {
      String payload = """
         {
          "userEmail": "user@mail.com",
          "contact": "292134525"
         }
         """;

      @Test
      @DisplayName("PUT[200] OK: Editing customer info.")
      void putCustomer() throws Exception {
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
   class CustomerAdministrationControllerDeleteResources {

      @Test
      @DisplayName("DELETE[204] No Content: Customer Deleted")
      void deleteCustomer() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + 1L))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());
         verify(softDelete, times(1)).softDeleteById(anyLong());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("DELETE[400] Bad Request: get customer /customers/{invalidPath}")
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
