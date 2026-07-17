package com.jame.dev.gymApp.subscription.controller;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.domain.exception.EntityNotFoundException;
import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.subscription.api.SubscriptionUserController;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.RetryResponse;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.StripeCheckoutService;
import com.jame.dev.gymApp.features.subscription.application.dto.PeriodDtoOutput;
import com.jame.dev.gymApp.features.subscription.application.support.handler.RetrySubscriptionPaymentHandler;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreatePaymentUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.FinalizeSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.RenewSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.SoftDeleteSubscriptionByIdUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetAllSubscriptionsByCustomerEmailUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByEmailSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByIdSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.*;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
   private RenewSubscriptionUseCase subscriptionRenew;

   @MockitoBean
   private FinalizeSubscriptionUseCase subscriptionFinalize;

   @MockitoBean
   private GetAllSubscriptionsByCustomerEmailUseCase subscriptionGetAllByCustomerEmail;

   @MockitoBean
   private StripeCheckoutService stripeCheckoutService;

   @MockitoBean
   private SoftDeleteSubscriptionByIdUseCase deleteById;

   @MockitoBean
   private IdentityExtractorService extractorService;

   @MockitoBean
   private CreatePaymentUseCase createPaymentUseCase;

   @MockitoBean
   private RetrySubscriptionPaymentHandler retrySubscriptionPaymentHandler;

   private final String URI_TEMPLATE = "/app/v1/subscriptions";
   private final String customerEmail = "user@mail.com";

   private final SubscriptionResponse subscriptionResponse = new SubscriptionResponse(
      1L, customerEmail,
      Membership.ANNUAL, BigDecimal.valueOf(3000d),
      List.of(new PeriodDtoOutput(Period.ANNUAL, LocalDate.now(), LocalDate.now().plusYears(1))),
      SubscriptionStatus.PAID
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
         var checkoutResponse = mock(SubscriptionCheckoutResponse.class);
         given(checkoutResponse.sessionUrl()).willReturn("https://stripe.com/session_123");
         given(checkoutResponse.sessionId()).willReturn("session_123");
         given(checkoutResponse.paymentIndent()).willReturn("pi_123");
         given(checkoutResponse.paymentSubscription()).willReturn("sub_123");
         given(stripeCheckoutService.createCheckoutSessionFrom(any(SubscriptionRequest.class))).willReturn(checkoutResponse);
         given(subscriptionCreate.create(any(SubscriptionRequest.class))).willReturn(subscriptionResponse);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.checkout").exists())
            .andExpect(jsonPath("$.checkout.sessionUrl").value("https://stripe.com/session_123"))
            .andExpect(jsonPath("$.subscription").exists())
             .andExpect(jsonPath("$.subscription.id").isNotEmpty());
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(SubscriptionRequest.class));
         verify(subscriptionCreate, times(1)).create(any(SubscriptionRequest.class));
         verify(createPaymentUseCase, times(1)).create(any());
      }

      @Test
      @DisplayName("POST[500]: Stripe session creation failed")
      void stripeSessionCreationFailed() throws Exception {
         given(stripeCheckoutService.createCheckoutSessionFrom(any(SubscriptionRequest.class)))
            .willThrow(StripeSessionException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.code").value("INTERNAL_OPERATION"));
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(SubscriptionRequest.class));
         verifyNoInteractions(subscriptionCreate);
      }

      @Test
      @DisplayName("POST[409]: Conflict: Subscription already exists")
      void alreadyExists() throws Exception {
         var response = mock(SubscriptionCheckoutResponse.class);
         given(stripeCheckoutService.createCheckoutSessionFrom(any(SubscriptionRequest.class)))
            .willReturn(response);
         given(subscriptionCreate.create(any(SubscriptionRequest.class))).willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(SubscriptionRequest.class));
         verify(subscriptionCreate, times(1)).create(any(SubscriptionRequest.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Subscription is deactivated")
      void subscriptionIsDeactivated() throws Exception {
         var response = mock(SubscriptionCheckoutResponse.class);
         given(stripeCheckoutService.createCheckoutSessionFrom(any(SubscriptionRequest.class)))
            .willReturn(response);
         given(subscriptionCreate.create(any(SubscriptionRequest.class))).willThrow(NoActiveException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(SubscriptionRequest.class));
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
         var checkoutResponse = mock(SubscriptionCheckoutResponse.class);
         given(checkoutResponse.sessionUrl()).willReturn("https://stripe.com/renew_123");
         given(checkoutResponse.sessionId()).willReturn("session_renew_123");
         given(checkoutResponse.paymentIndent()).willReturn("pi_renew_123");
         given(checkoutResponse.paymentSubscription()).willReturn("sub_renew_123");
         given(stripeCheckoutService.createCheckoutSessionFrom(any(SubscriptionRequest.class))).willReturn(checkoutResponse);
         given(subscriptionRenew.renew(anyLong(), any(SubscriptionRequest.class))).willReturn(subscriptionResponse);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(SubscriptionRequest.class));
         verify(subscriptionRenew, times(1)).renew(anyLong(), any(SubscriptionRequest.class));
         verify(createPaymentUseCase, times(1)).create(any());
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
   @DisplayName("POST Retry Subscription.")
   class SubscriptionUserControllerPostRetryTests {

      @Test
      @DisplayName("POST[200] OK: retry subscription payment")
      void retrySuccessfully() throws Exception {
         given(extractorService.extract(any())).willReturn(customerEmail);
         given(retrySubscriptionPaymentHandler.handleSubscriptionPaymentRetry(customerEmail))
            .willReturn(ResponseEntity.ok(new RetryResponse("https://stripe.com/retry_123")));
         mockMvc.perform(post(URI_TEMPLATE + "/retry")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionUrl").value("https://stripe.com/retry_123"));
         verify(extractorService, times(1)).extract(any());
         verify(retrySubscriptionPaymentHandler, times(1)).handleSubscriptionPaymentRetry(customerEmail);
      }

      @Test
      @DisplayName("POST[404] Not Found: retry - payment not found")
      void retryNotFound() throws Exception {
         given(extractorService.extract(any())).willReturn(customerEmail);
         given(retrySubscriptionPaymentHandler.handleSubscriptionPaymentRetry(customerEmail))
            .willThrow(EntityNotFoundException.class);
         mockMvc.perform(post(URI_TEMPLATE + "/retry")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         verify(extractorService, times(1)).extract(any());
         verify(retrySubscriptionPaymentHandler, times(1)).handleSubscriptionPaymentRetry(customerEmail);
      }
   }

   @Nested
   @DisplayName("GET Current Subscriptions.")
   class SubscriptionUserControllerGetCurrentTests {

      @Test
      @DisplayName("GET[200] OK: get current subscriptions /subscriptions/current")
      void getAllByCustomerEmail() throws Exception {
         PageDto<SubscriptionResponse> page = mock();
         given(page.content()).willReturn(List.of(subscriptionResponse));
         given(page.totalElements()).willReturn(1L);
         given(extractorService.extract(any())).willReturn(customerEmail);
         given(subscriptionGetAllByCustomerEmail.getAllByCustomerEmail(eq(customerEmail), any()))
            .willReturn(page);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/current")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").exists())
            .andExpect(jsonPath("$.content[0].id").isNotEmpty());
         verify(extractorService, times(1)).extract(any());
         verify(subscriptionGetAllByCustomerEmail, times(1)).getAllByCustomerEmail(eq(customerEmail), any());
      }

      @Test
      @DisplayName("GET[200] OK: get current subscriptions - empty page")
      void getAllByCustomerEmailEmpty() throws Exception {
         PageDto<SubscriptionResponse> page = mock();
         given(page.content()).willReturn(List.of());
         given(page.totalElements()).willReturn(0L);
         given(extractorService.extract(any())).willReturn(customerEmail);
         given(subscriptionGetAllByCustomerEmail.getAllByCustomerEmail(eq(customerEmail), any()))
            .willReturn(page);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + "/current")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty());
         verify(extractorService, times(1)).extract(any());
         verify(subscriptionGetAllByCustomerEmail, times(1)).getAllByCustomerEmail(eq(customerEmail), any());
      }
   }

   @Nested
   @DisplayName("DELETE Subscription Resources.")
   class SubscriptionUserControllerDeleteTests {

      @Test
      @DisplayName("DELETE[204] No Content: Subscription deleted")
      void dropSubscription() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + 1L))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());
         verify(deleteById, times(1)).softDeleteById(anyLong());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("DELETE[400] Bad Request: invalid id format")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         verifyNoInteractions(deleteById);
      }
   }
}
