package com.jame.dev.gymApp.user.controller;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.user.api.UserAdministrationController;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.usecases.mutation.*;
import com.jame.dev.gymApp.features.user.application.usecases.query.GetByIdUserUseCase;
import com.jame.dev.gymApp.features.user.application.usecases.query.GetPageUserUseCase;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.Role;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
class UserAdministrationControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private GetPageUserUseCase getPageUserUseCase;

   @MockitoBean
   private GetByIdUserUseCase getByIdUserUseCase;

   @MockitoBean
   private CreateUserUseCase createUserUseCase;

   @MockitoBean
   private UpdateUserUseCase updateUserUseCase;

   @MockitoBean
   private ReActivateUserByIdUseCase reActivateUserByIdUseCase;

   @MockitoBean
   private SoftDeleteUserByIdUseCase softDeleteUserByIdUseCase;

   @MockitoBean
   private HardDeleteUserByIdUseCase hardDeleteUserByIdUseCase;

   @MockitoBean
   private ApplicationEventPublisher applicationEventPublisher;

   private final String URI_TEMPLATE = "/app/v1/administration/users";

   private final UserResponse userDto = new UserResponse(
      1L, "dto", "dto@mail", AuthProvider.LOCAL, Set.of(Role.USER), false, null
   );

   private final UserMinimalInfoResponse userMinimalDto = new UserMinimalInfoResponse(
      1L, "dto", "dto@mail"
   );

   private final String payload = """
      {
         "name": "user",
         "email": "user@mail.com",
         "password": "password133",
         "authProvider": "LOCAL",
         "roles": ["USER"]
      }
      """;

   @Nested
   @DisplayName("GET: /app/v1/administration/users")
   class GetUsersPageTests {

      @Test
      @DisplayName("GET[200] OK: get page /users?page=0&size=1")
      void getPage() throws Exception {
         PageDto<UserResponse> page = mock();
         given(getPageUserUseCase.getPage(any(), any())).willReturn(page);

         mockMvc.perform(get(URI_TEMPLATE)
               .param("page", "0")
               .param("size", "1")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").exists());

         then(getPageUserUseCase).should(times(1)).getPage(any(), any());
      }

      @Test
      @DisplayName("GET[200] OK: get inactive page /users/inactive")
      void getInactivePage() throws Exception {
         PageDto<UserMinimalInfoResponse> page = mock();
         given(getPageUserUseCase.getInactivePage(any(), any())).willReturn(page);

         mockMvc.perform(get(URI_TEMPLATE + "/inactive")
               .param("page", "0")
               .param("size", "1")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").exists());

         then(getPageUserUseCase).should(times(1)).getInactivePage(any(), any());
      }

      @Nested
      @DisplayName("GET: /app/v1/administration/users/{id}")
      class GetUserByIdTests {

         @Test
         @DisplayName("GET[200] OK: /users/{id}")
         void getUser() throws Exception {
            given(getByIdUserUseCase.getById(anyLong())).willReturn(userDto);

            mockMvc.perform(get(URI_TEMPLATE + "/1")
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.*").exists());

            then(getByIdUserUseCase).should(times(1)).getById(anyLong());
         }

         @Test
         @DisplayName("GET[404] Not Found: /users/{id}")
         void userNotFound() throws Exception {
            given(getByIdUserUseCase.getById(anyLong())).willThrow(UserNotFoundException.class);

            mockMvc.perform(get(URI_TEMPLATE + "/100")
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.*").exists());

            then(getByIdUserUseCase).should(times(1)).getById(anyLong());
            then(getByIdUserUseCase).shouldHaveNoMoreInteractions();
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = TestDataSource.ID_RESOURCE_ERRORS,
            nullValues = "NULL")
         @DisplayName("GET[400] Bad Request: /users/{invalidPath}")
         void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
            mockMvc.perform(get(URI_TEMPLATE + "/" + value)
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(expectedCode));

            then(getByIdUserUseCase).shouldHaveNoInteractions();
         }
      }

      @Nested
      @DisplayName("POST: /app/v1/administration/users")
      class PostUserTests {

         @Test
         @DisplayName("POST[201] Created")
         void postUser() throws Exception {
            given(createUserUseCase.create(any(UserRequest.class))).willReturn(userDto);

            mockMvc.perform(post(URI_TEMPLATE)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(payload))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.id").isNotEmpty());

            then(createUserUseCase).should(times(1)).create(any(UserRequest.class));
            verifyNoMoreInteractions(createUserUseCase, applicationEventPublisher);
         }

         @Test
         @DisplayName("POST[409] Conflict: User already exists")
         void userAlreadyExists() throws Exception {
            given(createUserUseCase.create(any(UserRequest.class)))
               .willThrow(AlreadyExistsException.class);

            mockMvc.perform(post(URI_TEMPLATE)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(payload))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(409))
               .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));

            then(createUserUseCase).should(times(1)).create(any(UserRequest.class));
            then(applicationEventPublisher).shouldHaveNoInteractions();
         }

         @Test
         @DisplayName("POST[409] Conflict: User is deactivated")
         void userIsDeactivated() throws Exception {
            given(createUserUseCase.create(any(UserRequest.class)))
               .willThrow(NoActiveException.class);

            mockMvc.perform(post(URI_TEMPLATE)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(payload))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(409))
               .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));

            then(createUserUseCase).should(times(1)).create(any(UserRequest.class));
            then(applicationEventPublisher).shouldHaveNoInteractions();
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = TestDataSource.PAYLOAD_VALIDATIONS_ERRORS,
            nullValues = "NULL", emptyValue = "EMPTY")
         @DisplayName("POST[400] Bad Request: Invalid values inside payload")
         void badRequestInvalidPayloadValues(String email, String codeExpected) throws Exception {
            String invalidPayload = """
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
                  .content(invalidPayload))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(codeExpected));

            then(createUserUseCase).shouldHaveNoInteractions();
            then(applicationEventPublisher).shouldHaveNoInteractions();
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            nullValues = "NULL",
            emptyValue = "EMPTY",
            textBlock = TestDataSource.BODY_FORMAT_ERRORS)
         @DisplayName("POST[400] Bad Request: Invalid payload format")
         void badRequestInvalidPayload(String value, String codeExpected) throws Exception {
            mockMvc.perform(post(URI_TEMPLATE)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(value))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(codeExpected));

            then(createUserUseCase).shouldHaveNoInteractions();
            then(applicationEventPublisher).shouldHaveNoInteractions();
         }
      }

      @Nested
      @DisplayName("PUT: /app/v1/administration/users/{id}")
      class PutUserTests {

         @Test
         @DisplayName("PUT[200] OK: Editing user")
         void putUser() throws Exception {
            given(updateUserUseCase.update(anyLong(), any(UserRequest.class))).willReturn(userDto);

            mockMvc.perform(put(URI_TEMPLATE + "/1")
                  .accept(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(payload))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.*").exists());

            then(updateUserUseCase).should(times(1)).update(anyLong(), any(UserRequest.class));
         }

         @Test
         @DisplayName("PUT[404] Not Found")
         void userNotFound() throws Exception {
            given(updateUserUseCase.update(anyLong(), any(UserRequest.class)))
               .willThrow(UserNotFoundException.class);

            mockMvc.perform(put(URI_TEMPLATE + "/1")
                  .accept(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(payload))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(404));

            then(updateUserUseCase).should(times(1)).update(anyLong(), any(UserRequest.class));
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = TestDataSource.ID_RESOURCE_ERRORS,
            nullValues = "NULL")
         @DisplayName("PUT[400] Bad Request: invalid id format")
         void invalidId(String value, String expectedCode) throws Exception {
            mockMvc.perform(put(URI_TEMPLATE + "/" + value)
                  .accept(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(payload))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(expectedCode));

            then(updateUserUseCase).shouldHaveNoInteractions();
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = TestDataSource.PAYLOAD_VALIDATIONS_ERRORS,
            nullValues = "NULL", emptyValue = "EMPTY")
         @DisplayName("PUT[400] Bad Request: Invalid values inside payload")
         void badRequestInvalidPayloadValues(String email, String codeExpected) throws Exception {
            String invalidPayload = """
               {
                  "name": "user",
                  "email": "%s",
                  "password": "password133",
                  "authProvider": "LOCAL",
                  "roles": ["USER"]
               }
               """.formatted(email);

            mockMvc.perform(put(URI_TEMPLATE + "/1")
                  .accept(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(invalidPayload))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(codeExpected));

            then(updateUserUseCase).shouldHaveNoInteractions();
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            nullValues = "NULL",
            emptyValue = "EMPTY",
            textBlock = TestDataSource.BODY_FORMAT_ERRORS)
         @DisplayName("PUT[400] Bad Request: Invalid payload format")
         void badRequestInvalidPayload(String value, String codeExpected) throws Exception {
            mockMvc.perform(put(URI_TEMPLATE + "/1")
                  .accept(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(value))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(codeExpected));

            then(updateUserUseCase).shouldHaveNoInteractions();
         }
      }

      @Nested
      @DisplayName("PATCH: /app/v1/administration/users/{id}/recover")
      class PatchRecoverUserTests {

         @Test
         @DisplayName("PATCH[200] OK: Recover user")
         void recoverUser() throws Exception {
            mockMvc.perform(patch(URI_TEMPLATE + "/1/recover"))
               .andExpect(status().isOk());

            then(reActivateUserByIdUseCase).should(times(1)).reActivateById(anyLong());
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = TestDataSource.ID_RESOURCE_ERRORS,
            nullValues = "NULL")
         @DisplayName("PATCH[400] Bad Request: /users/{invalidId}/recover")
         void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
            mockMvc.perform(patch(URI_TEMPLATE + "/" + value + "/recover")
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(expectedCode));

            then(reActivateUserByIdUseCase).shouldHaveNoInteractions();
         }
      }

      @Nested
      @DisplayName("DELETE: /app/v1/administration/users")
      class DeleteUserTests {

         @Test
         @DisplayName("DELETE[204] No Content: Soft delete user")
         void deleteUser() throws Exception {
            mockMvc.perform(delete(URI_TEMPLATE + "/1"))
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$.*").doesNotExist());

            then(softDeleteUserByIdUseCase).should(times(1)).softDeleteById(anyLong());
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = TestDataSource.ID_RESOURCE_ERRORS,
            nullValues = "NULL")
         @DisplayName("DELETE[400] Bad Request: /users/{invalidPath}")
         void softDeleteInvalidPathVariable(String value, String expectedCode) throws Exception {
            mockMvc.perform(delete(URI_TEMPLATE + "/" + value)
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(expectedCode));

            then(softDeleteUserByIdUseCase).shouldHaveNoInteractions();
         }

         @Test
         @DisplayName("DELETE[204] No Content: Hard delete user")
         void deleteUserHard() throws Exception {
            mockMvc.perform(delete(URI_TEMPLATE + "/1/hard"))
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$.*").doesNotExist());

            then(hardDeleteUserByIdUseCase).should(times(1)).hardDeleteById(anyLong());
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = TestDataSource.ID_RESOURCE_ERRORS,
            nullValues = "NULL")
         @DisplayName("DELETE[400] Bad Request: /users/{invalidPath}/hard")
         void hardDeleteInvalidPathVariable(String value, String expectedCode) throws Exception {
            mockMvc.perform(delete(URI_TEMPLATE + "/" + value + "/hard")
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(expectedCode));

            then(hardDeleteUserByIdUseCase).shouldHaveNoInteractions();
         }
      }
   }
}