package com.jame.dev.gymApp.subscription.controller;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.subscription.api.SubscriptionAdministrationController;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.dto.PeriodDtoOutput;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.*;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByIdSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetPageSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.RenewSubscriptionException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionUnfinishedException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.service.SubscriptionNotificationAppService;
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
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = SubscriptionAdministrationController.class,
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
class SubscriptionAdministrationControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private SubscriptionAdministrationController controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private GetPageSubscriptionUseCase subscriptionGetPage;

   @MockitoBean
   private GetByIdSubscriptionUseCase subscriptionGetById;

   @MockitoBean
   private CreateSubscriptionUseCase subscriptionCreate;

   @MockitoBean
   private UpdateSubscriptionUseCase subscriptionUpdate;

   @MockitoBean
   private RenewSubscriptionUseCase subscriptionRenew;

   @MockitoBean
   private FinalizeSubscriptionUseCase subscriptionFinalize;

   @MockitoBean
   private SoftDeleteSubscriptionByIdUseCase subscriptionSoftDelete;

   @MockitoBean
   private SubscriptionNotificationAppService subsNotificationAppService;

   @MockitoBean
   private CompletedCheckoutUseCase completedCheckoutUseCase;

   @MockitoBean
   private CreatePaymentUseCase createPaymentUseCase;


   private final String URI_TEMPLATE = "/app/v1/administration/subs";
   private final String customerEmail = "user@mail.com";

   private final SubscriptionResponse subscriptionResponse = new SubscriptionResponse(
      1L, customerEmail,
      Membership.ANNUAL, BigDecimal.valueOf(3000d),
      List.of(new PeriodDtoOutput(Period.ANNUAL, LocalDate.now(), LocalDate.now().plusYears(1))),
      SubscriptionStatus.PAID
   );

   @Nested
   @DisplayName("GET Subscription Resources.")
   class SubscriptionAdministrationControllerGetResourceTests {

      @Test
      @DisplayName("GET[200] OK: get page /subs?page=0&size=1")
      void getPage() throws Exception {
         PageDto<SubscriptionResponse> page = mock();
         given(page.content()).willReturn(List.of());
         given(page.totalElements()).willReturn(0L);
      given(subscriptionGetPage.getPage(any(), any())).willReturn(page);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE)
               .param("page", "0")
               .param("size", "1")
               .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content").exists());
         verify(subscriptionGetPage, times(1)).getPage(any(), any());
      }

      @Test
      @DisplayName("GET[200] OK: get sub /subs/{id}")
      void getSubscription() throws Exception {
         given(subscriptionGetById.getById(anyLong())).willReturn(subscriptionResponse);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionGetById, times(1)).getById(anyLong());
      }

      @Test
      @DisplayName("GET[404] Not Found: get sub /subs/{id}")
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
      @DisplayName("GET[400] Bad Request: get sub /subs/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(subscriptionGetById);
      }
   }

   @Nested
   @DisplayName("POST Subscription Resources.")
   class SubscriptionAdministrationControllerPostResourceTests {

      private final String payload = """
         {
            "customerEmail": "user@mail.com",
            "membership": "ANNUAL"
         }
         """;

      @Test
      @DisplayName("POST[201] Created")
      void postSubscription() throws Exception {
         doNothing().when(completedCheckoutUseCase).execute(any(CompletedCheckoutEvent.class));
         given(subscriptionCreate.create(any(SubscriptionRequest.class))).willReturn(subscriptionResponse);
         given(subscriptionGetById.getById(anyLong())).willReturn(subscriptionResponse);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").isNotEmpty());
         verify(subscriptionCreate, times(1)).create(any(SubscriptionRequest.class));
         verify(completedCheckoutUseCase, times(1)).execute(any(CompletedCheckoutEvent.class));
         verify(createPaymentUseCase, times(1)).create(any());
         verify(subscriptionGetById, times(1)).getById(anyLong());
      }

      @Test
      @DisplayName("POST[409]: Conflict: Subscription already exists")
      void subscriptionAlreadyExists() throws Exception {
         given(subscriptionCreate.create(any(SubscriptionRequest.class))).willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         verify(subscriptionCreate, times(1)).create(any(SubscriptionRequest.class));
         verifyNoInteractions(completedCheckoutUseCase);
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
         verifyNoInteractions(completedCheckoutUseCase);
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
   @DisplayName("POST Notify Subscription.")
   class SubscriptionAdministrationControllerPostNotify {

      @Test
      @DisplayName("POST[200] OK: notify subscribers")
      void notifySubscribers() throws Exception {
         mockMvc.perform(post(URI_TEMPLATE + "/notify"))
            .andExpect(status().isOk());
         verify(subsNotificationAppService, times(1)).notifySubscriptionEnds();
         verifyNoMoreInteractions(subsNotificationAppService);
      }

      @Test
      @DisplayName("POST[401] Unauthorized: notify already done")
      void notifySubscribersAlreadyDone() throws Exception {
         doThrow(NotificationException.class).when(subsNotificationAppService).notifySubscriptionEnds();
         mockMvc.perform(post(URI_TEMPLATE + "/notify"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.code").value("UNSUPPORTED_OPERATION"));
         verify(subsNotificationAppService, times(1)).notifySubscriptionEnds();
      }
   }

   @Nested
   @DisplayName("PUT Subscription Resources.")
   class SubscriptionAdministrationControllerPutResources {
      String payload = """
         {
          "customerEmail": "user@mail.com",
          "membership": "ANNUAL"
         }
         """;

      @Test
      @DisplayName("PUT[200] OK: Editing subscription info.")
      void putSubscription() throws Exception {
         given(subscriptionUpdate.update(anyLong(), any(SubscriptionRequest.class))).willReturn(subscriptionResponse);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(subscriptionUpdate, times(1)).update(anyLong(), any(SubscriptionRequest.class));
      }

      @Test
      @DisplayName("PUT[404] Not Found")
      void subscriptionNotFound() throws Exception {
         given(subscriptionUpdate.update(anyLong(), any(SubscriptionRequest.class))).willThrow(SubscriptionNotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));
         verify(subscriptionUpdate, times(1)).update(anyLong(), any(SubscriptionRequest.class));
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
         verifyNoInteractions(subscriptionUpdate);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.SUBSCRIPTION_FORMAT_PAYLOAD_ERROR,
         nullValues = "NULL", emptyValue = "EMPTY")
      @DisplayName("PUT[400]: Bad Request: Invalid values inside payload.")
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
         verifyNoInteractions(subscriptionUpdate);
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
         verifyNoInteractions(subscriptionUpdate);
      }
   }

   @Nested
   @DisplayName("PUT Subscription Renew.")
   class SubscriptionAdministrationControllerPutRenew {
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
      @DisplayName("PUT[200] Ok: Renew Subscription /subs/{id}/renew")
      void renewSubscription() throws Exception {
         given(subscriptionRenew.renew(anyLong(), any(SubscriptionRequest.class))).willReturn(subscriptionResponse);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L + "/renew")
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
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L + "/renew")
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
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L + "/renew")
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
         mockMvc.perform(MockMvcRequestBuilders.put(URI_TEMPLATE + '/' + value + "/renew")
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
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L + "/renew")
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
               put(URI_TEMPLATE + '/' + 1L + "/renew")
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
   class SubscriptionAdministrationControllerPatchFinalize {
      @Test
      @DisplayName("PATCH[200] OK: Subscription finalized")
      void finalizeSubscription() throws Exception {
         SubscriptionResponse finalized = new SubscriptionResponse(
            1L, customerEmail,
            subscriptionResponse.membership(),
            subscriptionResponse.price(),
            subscriptionResponse.periods(),
            SubscriptionStatus.FINALIZED
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

   @Nested
   @DisplayName("DELETE Subscription Resources.")
   class SubscriptionAdministrationControllerDeleteResources {

      @Test
      @DisplayName("DELETE[204] No Content: Subscription Deleted")
      void deleteSubscription() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + 1L))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());
         verify(subscriptionSoftDelete, times(1)).softDeleteById(anyLong());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("DELETE[400] Bad Request: get subscription /subs/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(subscriptionSoftDelete);
      }
   }
}
