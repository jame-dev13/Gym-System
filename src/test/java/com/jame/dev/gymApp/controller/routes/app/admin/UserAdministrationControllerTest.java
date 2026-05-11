package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.features.user.api.UserAdministrationController;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.presentation.exception.ApiErrorResponseFactory;
import com.jame.dev.gymApp.presentation.exception.GlobalExceptionHandler;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotFoundException;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import com.jame.dev.gymApp.features.user.domain.model.Role;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = UserAdministrationController.class,
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
public class UserAdministrationControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private UserAdministrationController controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private UserService userService;

   @MockitoBean
   private ApplicationEventPublisher applicationEventPublisher;

   private final String URI_TEMPLATE = "/app/v1/administration/users";
   private final UserResponse userDto = new UserResponse(
      1L, "dto", "dto@mail", Set.of(Role.USER)
   );

   @Nested
   @DisplayName("GET User Resources.")
   class UserAdministrationControllerGetResourceTests {

      @Test
      @DisplayName("GET[200] OK: get page /users?page=0&size=1")
      void getPage() throws Exception {
         PageDto<UserResponse> page = mock();
         given(userService.getPage(any()))
            .willReturn(page);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE)
               .param("page", "0")
               .param("size", "1")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").exists());
         then(userService).should(times(1)).getPage(any());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.PAGINATION_ERRORS,
         nullValues = "NULL")
      @DisplayName("GET[400] Bad Request: users?page={invalidPage}&size={invalidSize}")
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
      @DisplayName("GET[200] OK: get users /users/{id}")
      void getUser() throws Exception {
         given(userService.getById(anyLong()))
            .willReturn(userDto);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         then(userService).should(times(1)).getById(anyLong());
      }

      @Test
      @DisplayName("GET[404] Not Found: get user /users/{id}")
      void userNotFound() throws Exception {
         given(userService.getById(anyLong()))
            .willThrow(UserNotFoundException.class);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 100L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         then(userService).should(times(1)).getById(anyLong());
         then(userService).shouldHaveNoMoreInteractions();
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("GET[400] Bad Request: get user /users/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(get(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         then(userService).shouldHaveNoInteractions();
      }

   }

   @Nested
   @DisplayName("POST User Resources.")
   class UserAdministrationControllerPostResourceTests {

      private final String payload = """
         {
            "name": "user",
            "email": "user@mail.com",
            "password": "password133",
            "authProvider": "LOCAL",
            "roles": ["USER"]
         }
         """;

      @Test
      @DisplayName("POST[201] Created")
      void postUser() throws Exception {
         given(userService.save(any(UserRequest.class)))
            .willReturn(userDto);

         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").isNotEmpty());

         then(userService).should(times(1)).save(any(UserRequest.class));
         verifyNoMoreInteractions(userService, applicationEventPublisher);
      }

      @Test
      @DisplayName("POST[409]: Conflict: User already exists")
      void userAlreadyExists() throws Exception {
         given(userService.save(any(UserRequest.class)))
            .willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         then(userService).should(times(1)).save(any(UserRequest.class));
         then(applicationEventPublisher).shouldHaveNoInteractions();
      }

      @Test
      @DisplayName("POST[409]: Conflict: User is deactivated")
      void userIsDeactivated() throws Exception {
         given(userService.save(any(UserRequest.class)))
            .willThrow(NoActiveException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("ACCESS_OPERATION"));
         then(userService).should(times(1)).save(any(UserRequest.class));
         then(applicationEventPublisher).shouldHaveNoInteractions();
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.PAYLOAD_VALIDATIONS_ERRORS,
         nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("POST[400]: Bad Request: Invalid values inside payload.")
      void badRequestInvalidPayloadValues(String email, String codeExpected) throws Exception {
         String payload = """
            {
               "name": "user",
               "email": "%s",
               "password": "password133",
               "authProvider": "LOCAL",
               "roles": ["USER"]
            }
            """.formatted(email);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
         then(userService).shouldHaveNoInteractions();
         then(applicationEventPublisher).shouldHaveNoInteractions();
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
         then(userService).shouldHaveNoInteractions();
         then(applicationEventPublisher).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("PUT User Resources.")
   class UserAdministrationControllerPutResources {

      String payload = """
         {
            "name": "user",
            "email": "user@mail.com",
            "password": "password133",
            "authProvider": "LOCAL",
            "roles": ["USER"]
         }
         """;

      @Test
      @DisplayName("PUT[200] OK: Editing user info.")
      void putUser() throws Exception {
         given(userService.update(anyLong(), any(UserRequest.class)))
            .willReturn(userDto);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());

         then(userService).should(times(1)).update(
            anyLong(),
            any(UserRequest.class));
      }

      @Test
      @DisplayName("PUT[404] Not Found")
      void userNotFound() throws Exception {
         given(userService.update(anyLong(), any(UserRequest.class)))
            .willThrow(UserNotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));
         then(userService).should(times(1)).update(
            anyLong(), any(UserRequest.class));
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
         then(userService).shouldHaveNoInteractions();
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
         then(userService).shouldHaveNoInteractions();
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
         then(userService).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("DELETE User Resources.")
   class UserAdministrationControllerDeleteResources {

      @Test
      @DisplayName("DELETE[204] No Content: User Deleted")
      void deleteUser() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + 1L))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());
         then(userService).should(times(1)).softDelete(anyLong());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("DELETE[400] Bad Request: get user /users/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         then(userService).shouldHaveNoInteractions();
      }

   }
}
