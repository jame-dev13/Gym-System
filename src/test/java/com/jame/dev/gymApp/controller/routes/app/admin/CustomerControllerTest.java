package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.GlobalExceptionHandler;
import com.jame.dev.gymApp.controller.routes.TestConfig;
import com.jame.dev.gymApp.controller.routes.TestDataSource;
import com.jame.dev.gymApp.controller.routes.TestValidationConfig;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.exception.NoActiveException;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.in.CustomerService;
import com.jame.dev.gymApp.shared.enums.Role;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CustomerController.class,
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
class CustomerControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private CustomerController controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private CustomerService customerService;

   private final String URI_TEMPLATE = "/app/v1/administration/customers";
   private final CustomerDtoOutput customerDtoOutput = new CustomerDtoOutput(
           1L, new UserDtoOutput(1L, "dto", "dto@mail", Set.of(Role.USER)), "25082525"
   );

   @Nested
   @DisplayName("GET Customer Resources.")
   class CustomerControllerGetResourceTests {

      @Test
      @DisplayName("GET[200] OK: get page /customers?page=0&size=1")
      void getPage() throws Exception {
         Pageable pageable = PageRequest.of(0, 1);
         PageDto<CustomerDtoOutput> page = new PageDto<>(
                 List.of(customerDtoOutput),
                 pageable.getPageNumber(),
                 pageable.getPageSize(),
                 1,
                 "id",
                 "ASC"
         );
         given(customerService.getPage(any()))
                 .willReturn(page);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE)
                         .param("page", "0")
                         .param("size", "1")
                         .accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.content").exists());
         then(customerService).should(times(1)).getPage(any());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
              textBlock = TestDataSource.PAGINATION_ERRORS,
              nullValues = "NULL")
      @DisplayName("GET[400] Bad Request: customer?page={invalidPage}&size={invalidSize}")
      void invalidPageParams(String page, String size, String errorCodeExpected) throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE)
                         .param("page", String.valueOf(page))
                         .param("size", String.valueOf(size))
                         .accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.*").exists())
                 .andExpect(jsonPath("$.status").value(400))
                 .andExpect(jsonPath("$.code").value(errorCodeExpected));
      }

      @Test
      @DisplayName("GET[200] OK: get customer /customers/{id}")
      void getCustomer() throws Exception {
         given(customerService.getById(anyLong()))
                 .willReturn(Optional.of(customerDtoOutput));
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 1L)
                         .accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.*").exists());
         then(customerService).should(times(1)).getById(anyLong());
      }

      @Test
      @DisplayName("GET[400] Not Found: get customer /customers/{id}")
      void customerNotFound() throws Exception {
         given(customerService.getById(anyLong()))
                 .willReturn(Optional.empty());
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 100L)
                         .accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isNotFound())
                 .andExpect(jsonPath("$.*").exists());
         then(customerService).should(times(1)).getById(anyLong());
         then(customerService).shouldHaveNoMoreInteractions();
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
         then(customerService).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("POST Customer Resources.")
   class CustomerControllerPostResourceTests {

      private final String payload = """
              {
                 "userEmail": "user@mail.com",
                 "contact": "13075523"
              }
              """;

      @Test
      @DisplayName("POST[201] Created")
      void postCustomer() throws Exception {
         given(customerService.save(any(CustomerDtoInput.class)))
                 .willReturn(customerDtoOutput);
         mockMvc.perform(post(URI_TEMPLATE)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(payload))
                 .andExpect(status().isCreated())
                 .andExpect(jsonPath("$.*").exists())
                 .andExpect(jsonPath("$.id").isNotEmpty());
         then(customerService).should(times(1)).save(any(CustomerDtoInput.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Customer already exists")
      void customerAlreadyExists() throws Exception {
         given(customerService.save(any(CustomerDtoInput.class)))
                 .willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(payload))
                 .andExpect(status().isConflict())
                 .andExpect(jsonPath("$.*").exists())
                 .andExpect(jsonPath("$.status").value(409))
                 .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         then(customerService).should(times(1)).save(any(CustomerDtoInput.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Customer is deactivated")
      void customerIsDeactivated() throws Exception {
         given(customerService.save(any(CustomerDtoInput.class)))
                 .willThrow(NoActiveException.class);
         mockMvc.perform(post(URI_TEMPLATE)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(payload))
                 .andExpect(status().isConflict())
                 .andExpect(jsonPath("$.*").exists())
                 .andExpect(jsonPath("$.status").value(409))
                 .andExpect(jsonPath("$.code").value("ACCESS_OPERATION"));
         then(customerService).should(times(1)).save(any(CustomerDtoInput.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
              textBlock = TestDataSource.PAYLOAD_VALIDATIONS_ERRORS,
              nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("POST[400]: Bad Request: Invalid values inside payload.")
      void badRequestInvalidPayloadValues(String email, String codeExpected) throws Exception {
         //contact is @Nullable
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
         then(customerService).shouldHaveNoInteractions();
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
         then(customerService).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("PUT Customer Resources.")
   class CustomerControllerPutResources {
      String payload = """
              {
               "userEmail": "user@mail.com",
               "contact": "292134525"
              }
              """;

      @Test
      @DisplayName("PUT[200] OK: Editing customer info.")
      void putCustomer() throws Exception {
         given(customerService.update(anyLong(), any(CustomerDtoInput.class)))
                 .willReturn(customerDtoOutput);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
                         .accept(MediaType.APPLICATION_JSON)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(payload))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.*").exists());

         then(customerService).should(times(1)).update(
                 anyLong(),
                 any(CustomerDtoInput.class));
      }

      @Test
      @DisplayName("PUT[404] Not Found")
      void customerNotFound() throws Exception {
         given(customerService.update(anyLong(), any(CustomerDtoInput.class)))
                 .willThrow(CustomerNotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
                         .accept(MediaType.APPLICATION_JSON)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(payload))
                 .andExpect(status().isNotFound())
                 .andExpect(jsonPath("$.*").exists())
                 .andExpect(jsonPath("$.status").value(404));
         then(customerService).should(times(1)).update(
                 anyLong(), any(CustomerDtoInput.class));
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
         then(customerService).shouldHaveNoInteractions();
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
         then(customerService).shouldHaveNoInteractions();
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
         then(customerService).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("DELETE Customer Resources.")
   class CustomerControllerDeleteResources {

      @Test
      @DisplayName("DELETE[204] No Content: Customer Deleted")
      void deleteCustomer() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + 1L))
                 .andExpect(status().isNoContent())
                 .andExpect(jsonPath("$.*").doesNotExist());
         then(customerService).should(times(1)).softDelete(anyLong());
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
         then(customerService).shouldHaveNoInteractions();
      }

   }
}