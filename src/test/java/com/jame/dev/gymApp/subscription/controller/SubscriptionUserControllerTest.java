package com.jame.dev.gymApp.subscription.controller;

import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.subscription.api.SubscriptionUserController;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.dto.PeriodDtoOutput;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.FinalizeSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.RenewSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.UpdateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByEmailSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByIdSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.RenewSubscriptionException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionUnfinishedException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;
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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = SubscriptionUserController.class,
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
class SubscriptionUserControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private SubscriptionUserController controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private GetByIdSubscriptionUseCase subscriptionGetById;

   @MockitoBean
   private GetByEmailSubscriptionUseCase subscriptionGetByEmail;

   @MockitoBean
   private CreateSubscriptionUseCase subscriptionCreate;

   @MockitoBean
   private UpdateSubscriptionUseCase subscriptionUpdate;

   @MockitoBean
   private RenewSubscriptionUseCase subscriptionRenew;

   @MockitoBean
   private FinalizeSubscriptionUseCase subscriptionFinalize;

   private final String URI_TEMPLATE = "/app/v1/subscriptions";
   private final CustomerResponse customerResponse = new CustomerResponse(
      1L, new UserResponse(1L, "dto", "dto@mail", AuthProvider.LOCAL, Set.of(Role.USER)), "25082525"
   );

   private final SubscriptionResponse subscriptionResponse = new SubscriptionResponse(
      1L, customerResponse,
      Membership.ANNUAL, BigDecimal.valueOf(3000d),
      List.of(new PeriodDtoOutput(1L, Period.ANNUAL, LocalDate.now(), LocalDate.now().plusYears(1))),
      false
   );

   @Nested
   @DisplayName("GET Subscription Resources.")
   class SubscriptionUserControllerGetResourceTests {

      @Test
      @DisplayName("GET[200] OK: get subscription /subscriptions/{id}")
      void getById() throws Exception {
         given(subscriptionGetById.getById(anyLong())).willReturn(subscriptionResponse);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionGetById, times(1)).getById(anyLong());
      }

      @Test
      @DisplayName("GET[404] Not Found: get subscription /subscriptions/{id}")
      void subscriptionNotFound() throws Exception {
         given(subscriptionGetById.getById(anyLong())).willThrow(SubscriptionNotFoundException.class);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 100L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionGetById, times(1)).getById(anyLong());
         verifyNoMoreInteractions(subscriptionGetById);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("GET[400] Bad Request: get subscription /subscriptions/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(subscriptionGetById);
      }

      @Test
      @DisplayName("GET[200] OK: get subscription by email /subscriptions/{email}/customers")
      void getByEmail() throws Exception {
         given(subscriptionGetByEmail.getByEmail(anyString())).willReturn(subscriptionResponse);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/user@mail.com/customers")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionGetByEmail, times(1)).getByEmail(anyString());
      }

      @Test
      @DisplayName("GET[404] Not Found: get subscription by email /subscriptions/{email}/customers")
      void subscriptionNotFoundByEmail() throws Exception {
         given(subscriptionGetByEmail.getByEmail(anyString())).willThrow(SubscriptionNotFoundException.class);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/missing@mail.com/customers")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionGetByEmail, times(1)).getByEmail(anyString());
         verifyNoMoreInteractions(subscriptionGetByEmail);
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
      @DisplayName("GET[400] Bad Request: get subscription by email /subscriptions/{invalidEmail}/customers")
      void badRequestInvalidEmailPathVariable(String email, String expectedCode) throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/" + email + "/customers")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(subscriptionGetByEmail);
      }
   }

   @Nested
   @DisplayName("POST Subscription Resources.")
   class SubscriptionUserControllerPostResourceTests {

      private final String payload = """
         {
            "customerEmail": "user@mail.com",
            "membership": "ANNUAL"
         }
         """;

      @Test
      @DisplayName("POST[201] Created")
      void create() throws Exception {
         given(subscriptionCreate.create(any(SubscriptionRequest.class))).willReturn(subscriptionResponse);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").isNotEmpty());
         verify(subscriptionCreate, times(1)).create(any(SubscriptionRequest.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Subscription already exists")
      void alreadyExists() throws Exception {
         given(subscriptionCreate.create(any(SubscriptionRequest.class))).willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         verify(subscriptionCreate, times(1)).create(any(SubscriptionRequest.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Subscription is deactivated")
      void subscriptionIsDeactivated() throws Exception {
         given(subscriptionCreate.create(any(SubscriptionRequest.class))).willThrow(NoActiveException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verify(subscriptionCreate, times(1)).create(any(SubscriptionRequest.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.SUBSCRIPTION_FORMAT_PAYLOAD_ERROR,
         nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("POST[400]: Bad Request: Invalid values inside payload.")
      void badRequestInvalidPayloadValues(String email, String membership, String codeExpected) throws Exception {
         String payload = """
            {
               "customerEmail": "%s",
               "membership": "%s"
            }
            """.formatted(email, membership);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
         verifyNoInteractions(subscriptionCreate);
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
         verifyNoInteractions(subscriptionCreate);
      }
   }

   @Nested
   @DisplayName("PUT Subscription Renew.")
   class SubscriptionUserControllerPutRenew {
      String payload = """
         {
          "customerEmail": "user@mail.com",
          "membership": "ANNUAL"
         }
         """;

      static Stream<Arguments> renewExceptions() {
         int[] codes = {409, 404};
         return Stream.of(
            Arguments.of(SubscriptionUnfinishedException.class, codes[0]),
            Arguments.of(MissMatchException.class, codes[0]),
            Arguments.of(RenewSubscriptionException.class, codes[0]),
            Arguments.of(PricingNotFoundException.class, codes[1])
         );
      }

      @Test
      @DisplayName("PUT[200] Ok: Renew Subscription /subscriptions/{id}")
      void renew() throws Exception {
         given(subscriptionRenew.renew(anyLong(), any(SubscriptionRequest.class))).willReturn(subscriptionResponse);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionRenew, times(1)).renew(anyLong(), any(SubscriptionRequest.class));
      }

      @Test
      @DisplayName("PUT[409] Conflict: Subscription active")
      void subscriptionActive() throws Exception {
         given(subscriptionRenew.renew(anyLong(), any(SubscriptionRequest.class))).willThrow(SubscriptionUnfinishedException.class);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409));
         verify(subscriptionRenew, times(1)).renew(anyLong(), any(SubscriptionRequest.class));
      }

      @ParameterizedTest
      @MethodSource("renewExceptions")
      @DisplayName("PUT[409 | 404]: Cannot renew.")
      void renewNotAllowed(Class<? extends Throwable> exception, int code) throws Exception {
         given(subscriptionRenew.renew(anyLong(), any(SubscriptionRequest.class))).willThrow(exception);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().is(code))
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(code));
         verify(subscriptionRenew, atLeastOnce()).renew(anyLong(), any(SubscriptionRequest.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("PUT[400] Bad Request: renew - invalid id format")
      void invalidId(String value, String expectedCode) throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.put(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(subscriptionRenew);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.SUBSCRIPTION_FORMAT_PAYLOAD_ERROR,
         nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("PUT[400]: Bad Request: renew - Invalid values inside payload.")
      void badRequestInvalidPayloadValues(String email, String membership, String codeExpected) throws Exception {
         String payload = """
            {
               "customerEmail": "%s",
               "membership": "%s"
            }
            """.formatted(email, membership);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
         verifyNoInteractions(subscriptionRenew);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         nullValues = "NULL",
         emptyValue = "EMPTY",
         textBlock = TestDataSource.BODY_FORMAT_ERRORS)
      @DisplayName("PUT[400]: Bad Request: renew - Invalid payload")
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
         verifyNoInteractions(subscriptionRenew);
      }
   }

   @Nested
   @DisplayName("PATCH")
   class SubscriptionUserControllerPatchFinalize {
      @Test
      @DisplayName("PATCH[200] OK: Subscription finalized")
      void finalizeSubscription() throws Exception {
         SubscriptionResponse finalized = new SubscriptionResponse(
            1L, customerResponse,
            subscriptionResponse.membership(),
            subscriptionResponse.price(),
            subscriptionResponse.periods(),
            true
         );
         given(subscriptionFinalize.finalize(anyLong())).willReturn(finalized);
         mockMvc.perform(patch(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionFinalize, times(1)).finalize(anyLong());
      }

      @Test
      @DisplayName("PATCH[404] Not Found: Subscription not found")
      void subscriptionNotFound() throws Exception {
         given(subscriptionFinalize.finalize(anyLong())).willThrow(SubscriptionNotFoundException.class);
         mockMvc.perform(patch(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionFinalize, times(1)).finalize(anyLong());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("PATCH[400] Bad Request: invalid id format")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(patch(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(subscriptionFinalize);
      }
   }
}
