package com.jame.dev.gymApp.subscription.controller;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.subscription.api.SubscriptionCurrentController;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.StripeCheckoutService;
import com.jame.dev.gymApp.features.subscription.application.dto.PeriodResponse;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreatePaymentUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.CreateCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.DeleteCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.FinalizeCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.RenewCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetSubscriptionByCurrentUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.RenewSubscriptionException;
import com.jame.dev.gymApp.features.subscription.domain.exception.StripeSessionException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = SubscriptionCurrentController.class,
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
class SubscriptionCurrentControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private GetSubscriptionByCurrentUseCase subscriptionByCurrentUseCase;

   @MockitoBean
   private CreateCurrentSubscriptionUseCase subscriptionCreate;

   @MockitoBean
   private RenewCurrentSubscriptionUseCase subscriptionRenew;

   @MockitoBean
   private FinalizeCurrentSubscriptionUseCase subscriptionFinalize;

   @MockitoBean
   private DeleteCurrentSubscriptionUseCase subscriptionDelete;

   @MockitoBean
   private StripeCheckoutService stripeCheckoutService;

   @MockitoBean
   private CreatePaymentUseCase createPaymentUseCase;

   private final String URI_TEMPLATE = "/app/v1/subscriptions/current";
   private final String customerEmail = "user@mail.com";

   private final SubscriptionCheckoutResponse checkoutResponse = SubscriptionCheckoutResponse.builder()
      .sessionUrl("https://stripe.com/session_123")
      .sessionId("session_123")
      .paymentIndent("pi_123")
      .paymentSubscription("sub_123")
      .build();

   private final SubscriptionResponse subscriptionResponse = new SubscriptionResponse(
      1L, customerEmail,
      Membership.ANNUAL, BigDecimal.valueOf(3000d),
      List.of(new PeriodResponse(Period.ANNUAL, LocalDate.now() + " - " + LocalDate.now().plusYears(1))),
      SubscriptionStatus.PAID
   );

   private final SubscriptionResponse finalizedResponse = new SubscriptionResponse(
      1L, customerEmail,
      Membership.ANNUAL, BigDecimal.valueOf(3000d),
      List.of(new PeriodResponse(Period.ANNUAL, LocalDate.now() + " - " + LocalDate.now().plusYears(1))),
      SubscriptionStatus.FINALIZED
   );

   @Nested
   @DisplayName("GET /app/v1/subscriptions/current")
   class SubscriptionCurrentControllerGetTests {
      @Test
      @DisplayName("GET[200] Get Current")
      void getCurrent() throws Exception {
         given(subscriptionByCurrentUseCase.getCurrent(any()))
            .willReturn(subscriptionResponse);

         assertDoesNotThrow(() -> mockMvc.perform(get(URI_TEMPLATE)
            .accept(MediaType.APPLICATION_JSON)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.id").exists(),
               jsonPath("$.id").value(subscriptionResponse.id())
            );

         verify(subscriptionByCurrentUseCase, times(1)).getCurrent(any());
         verifyNoMoreInteractions(subscriptionByCurrentUseCase);
      }

      @Test
      @DisplayName("GET404] Subscription Not Found")
      void getCurrent_should_throws_NotFoundException() throws Exception {
         willThrow(NotFoundException.class)
            .given(subscriptionByCurrentUseCase)
            .getCurrent(any());
         mockMvc.perform(get(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpectAll(
               jsonPath("$.*").exists(),
               jsonPath("$.status").value(404),
               jsonPath("$.code").value("NOT_FOUND_OPERATION")
            );

         verify(subscriptionByCurrentUseCase, times(1)).getCurrent(any());
         verifyNoMoreInteractions(subscriptionByCurrentUseCase);
      }


      @Test
      @DisplayName("GET[401] Authentication's null")
      void getCurrent_should_throws_AuthenticationNullException() throws Exception {
         willThrow(AuthenticationNullException.class)
            .given(subscriptionByCurrentUseCase).getCurrent(any());
         mockMvc.perform(get(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpectAll(
               jsonPath("$.*").exists(),
               jsonPath("$.status").value(401),
               jsonPath("$.code").value("AUTHENTICATION_OPERATION")
            );

         verify(subscriptionByCurrentUseCase, times(1)).getCurrent(any());
         verifyNoMoreInteractions(subscriptionByCurrentUseCase);
      }
   }

   @Nested
   @DisplayName("POST /app/v1/subscriptions/current")
   class SubscriptionCurrentControllerCreateTests {

      private final String payload = """
         {
            "membership": "ANNUAL"
         }
         """;

      @Test
      @DisplayName("POST[201] Created")
      void create() throws Exception {
         given(stripeCheckoutService.createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class)))
            .willReturn(checkoutResponse);
         given(subscriptionCreate.create(any(), any(SubscriptionCurrentRequest.class)))
            .willReturn(subscriptionResponse);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.checkout").exists())
            .andExpect(jsonPath("$.checkout.sessionUrl").value("https://stripe.com/session_123"))
            .andExpect(jsonPath("$.subscription").exists())
            .andExpect(jsonPath("$.subscription.id").value(1));
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class));
         verify(subscriptionCreate, times(1)).create(any(), any(SubscriptionCurrentRequest.class));
         verify(createPaymentUseCase, times(1)).create(any());
      }

      @Test
      @DisplayName("POST[500]: Stripe session creation failed")
      void stripeSessionCreationFailed() throws Exception {
         given(stripeCheckoutService.createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class)))
            .willThrow(StripeSessionException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isInternalServerError());
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class));
         verifyNoInteractions(subscriptionCreate);
      }

      @Test
      @DisplayName("POST[409] Conflict: Subscription already exists")
      void alreadyExists() throws Exception {
         given(stripeCheckoutService.createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class)))
            .willReturn(checkoutResponse);
         given(subscriptionCreate.create(any(), any(SubscriptionCurrentRequest.class)))
            .willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class));
         verify(subscriptionCreate, times(1)).create(any(), any(SubscriptionCurrentRequest.class));
         verifyNoInteractions(createPaymentUseCase);
      }

      @ParameterizedTest
      @CsvSource({"BANANA", "''"})
      @DisplayName("POST[400]: Bad Request: Invalid membership value")
      void badRequestInvalidMembership(String membership) throws Exception {
         String payload = """
            {
               "membership": "%s"
            }
            """.formatted(membership);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verifyNoInteractions(subscriptionCreate);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.BODY_FORMAT_ERRORS,
         nullValues = "NULL",
         emptyValue = "EMPTY")
      @DisplayName("POST[400]: Bad Request: Invalid payload")
      void badRequestInvalidPayload(String value, String codeExpected) throws Exception {
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(value))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
         verifyNoInteractions(subscriptionCreate);
      }

      @Test
      @DisplayName("POST[400] Bad Request: null body")
      void badRequestNullBody() throws Exception {
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content("null"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verifyNoInteractions(subscriptionCreate);
      }
   }

   @Nested
   @DisplayName("PUT /app/v1/subscriptions/current")
   class SubscriptionCurrentControllerRenewTests {

      private final String payload = """
         {
            "membership": "ANNUAL"
         }
         """;

      @Test
      @DisplayName("PUT[200] OK: renew subscription")
      void renew() throws Exception {
         given(stripeCheckoutService.createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class)))
            .willReturn(checkoutResponse);
         given(subscriptionRenew.renew(any(), any(SubscriptionCurrentRequest.class)))
            .willReturn(subscriptionResponse);
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.checkout.sessionUrl").value("https://stripe.com/session_123"))
            .andExpect(jsonPath("$.subscription.id").value(1));
         verify(stripeCheckoutService, times(1)).createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class));
         verify(subscriptionRenew, times(1)).renew(any(), any(SubscriptionCurrentRequest.class));
         verify(createPaymentUseCase, times(1)).create(any());
      }

      @Test
      @DisplayName("PUT[404] Not Found: Subscription not found")
      void renewNotFound() throws Exception {
         given(stripeCheckoutService.createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class)))
            .willReturn(checkoutResponse);
         given(subscriptionRenew.renew(any(), any(SubscriptionCurrentRequest.class)))
            .willThrow(NotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("NOT_FOUND_OPERATION"));
         verify(subscriptionRenew, times(1)).renew(any(), any(SubscriptionCurrentRequest.class));
      }

      @Test
      @DisplayName("PUT[404] Not Found: Pricing not found")
      void renewPricingNotFound() throws Exception {
         given(stripeCheckoutService.createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class)))
            .willReturn(checkoutResponse);
         given(subscriptionRenew.renew(any(), any(SubscriptionCurrentRequest.class)))
            .willThrow(PricingNotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("NOT_FOUND_OPERATION"));
         verify(subscriptionRenew, times(1)).renew(any(), any(SubscriptionCurrentRequest.class));
      }

      @Test
      @DisplayName("PUT[409] Conflict: Cannot renew now")
      void renewConflict() throws Exception {
         given(stripeCheckoutService.createCheckoutSessionFrom(any(), any(SubscriptionCurrentRequest.class)))
            .willReturn(checkoutResponse);
         given(subscriptionRenew.renew(any(), any(SubscriptionCurrentRequest.class)))
            .willThrow(RenewSubscriptionException.class);
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("UPDATE_OPERATION"));
         verify(subscriptionRenew, times(1)).renew(any(), any(SubscriptionCurrentRequest.class));
         verifyNoInteractions(createPaymentUseCase);
      }

      @ParameterizedTest
      @CsvSource({"BANANA", "''"})
      @DisplayName("PUT[400]: Bad Request: Invalid membership value")
      void badRequestInvalidMembership(String membership) throws Exception {
         String payload = """
            {
               "membership": "%s"
            }
            """.formatted(membership);
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("VALIDATION_OPERATION"));
         verifyNoInteractions(subscriptionRenew);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.BODY_FORMAT_ERRORS,
         nullValues = "NULL",
         emptyValue = "EMPTY")
      @DisplayName("PUT[400]: Bad Request: Invalid payload")
      void badRequestInvalidPayload(String value, String codeExpected) throws Exception {
         mockMvc.perform(put(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(value))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));
         verifyNoInteractions(subscriptionRenew);
      }
   }

   @Nested
   @DisplayName("PATCH /app/v1/subscriptions/current")
   class SubscriptionCurrentControllerFinalizeTests {

      @Test
      @DisplayName("PATCH[200] OK: Subscription finalized")
      void finalizeSubscription() throws Exception {
         given(subscriptionFinalize.finalizeCurrent(any())).willReturn(finalizedResponse);
         mockMvc.perform(patch(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value("FINALIZED"));
         verify(subscriptionFinalize, times(1)).finalizeCurrent(any());
      }

      @Test
      @DisplayName("PATCH[404] Not Found: Subscription not found")
      void finalizeNotFound() throws Exception {
         given(subscriptionFinalize.finalizeCurrent(any())).willThrow(NotFoundException.class);
         mockMvc.perform(patch(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("NOT_FOUND_OPERATION"));
         verify(subscriptionFinalize, times(1)).finalizeCurrent(any());
      }
   }

   @Nested
   @DisplayName("DELETE /app/v1/subscriptions/current")
   class SubscriptionCurrentControllerDeleteTests {

      @Test
      @DisplayName("DELETE[204] No Content: Subscription deleted")
      void dropSubscription() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());
         verify(subscriptionDelete, times(1)).delete(any());
      }
   }
}