package com.jame.dev.gymApp.controller.routes.app.admin;


import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.GlobalExceptionHandler;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.*;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import com.jame.dev.gymApp.shared.enums.Membership;
import com.jame.dev.gymApp.shared.enums.Period;
import com.jame.dev.gymApp.shared.enums.Role;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = SubscriptionController.class,
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
public class SubscriptionControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private SubscriptionController controller;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private SubscriptionService subscriptionService;

   private final String URI_TEMPLATE = "/app/v1/administration/subs";
   private final CustomerDtoOutput customerDtoOutput = new CustomerDtoOutput(
      1L, new UserDtoOutput(1L, "dto", "dto@mail", Set.of(Role.USER)), "25082525"
   );

   private final SubscriptionDtoOutput subscriptionDtoOutput = new SubscriptionDtoOutput(
      1L, customerDtoOutput,
      Membership.ANNUAL, BigDecimal.valueOf(3000d),
      List.of(new PeriodDtoOutput(1L, Period.ANNUAL, LocalDate.now(), LocalDate.now().plusYears(1))),
      false
   );

   @Nested
   @DisplayName("GET Subscription Resources.")
   class SubscriptionControllerGetResourceTests {

      @Test
      @DisplayName("GET[200] OK: get page /subs?page=0&size=1")
      void getPage() throws Exception {
         PageDto<SubscriptionDtoOutput> page = mock();
         given(subscriptionService.getPage(any()))
            .willReturn(page);
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE)
               .param("page", "0")
               .param("size", "1")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").exists());
         then(subscriptionService).should(times(1)).getPage(any());
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.PAGINATION_ERRORS,
         nullValues = "NULL")
      @DisplayName("GET[400] Bad Request: subs?page={invalidPage}&size={invalidSize}")
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
      @DisplayName("GET[200] OK: get sub /subs/{id}")
      void getSubscription() throws Exception {
         given(subscriptionService.getById(anyLong()))
            .willReturn(Optional.of(subscriptionDtoOutput));
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         then(subscriptionService).should(times(1)).getById(anyLong());
      }

      @Test
      @DisplayName("GET[400] Not Found: get sub /subs/{id}")
      void subscriptionNotFound() throws Exception {
         given(subscriptionService.getById(anyLong()))
            .willReturn(Optional.empty());
         mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE + '/' + 100L)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         then(subscriptionService).should(times(1)).getById(anyLong());
         then(subscriptionService).shouldHaveNoMoreInteractions();
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.ID_RESOURCE_ERRORS,
         nullValues = "NULL")
      @DisplayName("GET[400] Bad Request: get sub /subs/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(get(URI_TEMPLATE + '/' + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));
         then(subscriptionService).shouldHaveNoInteractions();
      }

   }

   @Nested
   @DisplayName("POST Subscription Resources.")
   class SubscriptionControllerPostResourceTests {

      private final String payload = """
         {
            "customerEmail": "user@mail.com",
            "membership": "ANNUAL"
         }
         """;

      @Test
      @DisplayName("POST[201] Created")
      void postSubscription() throws Exception {
         given(subscriptionService.save(any(SubscriptionDtoInput.class)))
            .willReturn(subscriptionDtoOutput);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.id").isNotEmpty());
         then(subscriptionService).should(times(1)).save(any(SubscriptionDtoInput.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Subscription already exists")
      void subscriptionAlreadyExists() throws Exception {
         given(subscriptionService.save(any(SubscriptionDtoInput.class)))
            .willThrow(AlreadyExistsException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("SAVE_OPERATION"));
         then(subscriptionService).should(times(1)).save(any(SubscriptionDtoInput.class));
      }

      @Test
      @DisplayName("POST[409]: Conflict: Subscription is deactivated")
      void subscriptionIsDeactivated() throws Exception {
         given(subscriptionService.save(any(SubscriptionDtoInput.class)))
            .willThrow(NoActiveException.class);
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.code").value("ACCESS_OPERATION"));
         then(subscriptionService).should(times(1)).save(any(SubscriptionDtoInput.class));
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
         then(subscriptionService).shouldHaveNoInteractions();
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
         then(subscriptionService).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("PUT Subscription Resources.")
   class SubscriptionControllerPutResources {
      String payload = """
         {
          "customerEmail": "user@mail.com",
          "membership": "ANNUAL"
         }
         """;

      @Test
      @DisplayName("PUT[200] OK: Editing subscription info.")
      void putSubscription() throws Exception {
         given(subscriptionService.update(anyLong(), any(SubscriptionDtoInput.class)))
            .willReturn(subscriptionDtoOutput);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());

         then(subscriptionService).should(times(1)).update(
            anyLong(),
            any(SubscriptionDtoInput.class));
      }

      @Test
      @DisplayName("PUT[404] Not Found")
      void subscriptionNotFound() throws Exception {
         given(subscriptionService.update(anyLong(), any(SubscriptionDtoInput.class)))
            .willThrow(SubscriptionNotFoundException.class);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));
         then(subscriptionService).should(times(1)).update(
            anyLong(), any(SubscriptionDtoInput.class));
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
         then(subscriptionService).shouldHaveNoInteractions();
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
         then(subscriptionService).shouldHaveNoInteractions();
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
         then(subscriptionService).shouldHaveNoInteractions();
      }

   }

   @Nested
   @DisplayName("PUT Subscription Renew.")
   class SubscriptionControllerPutRenew {
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
         given(subscriptionService.put(anyLong(), any()))
            .willReturn(subscriptionDtoOutput);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L + "/renew")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());

         then(subscriptionService).should(times(1)).put(
            anyLong(),
            any(SubscriptionDtoInput.class));
         then(subscriptionService).shouldHaveNoMoreInteractions();
      }

      @Test
      @DisplayName("PUT[409] Conflict: Subscription active")
      void subscriptionActive() throws Exception {
         given(subscriptionService.put(anyLong(), any()))
            .willThrow(SubscriptionUnfinishedException.class);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L + "/renew")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409));
         then(subscriptionService).should(times(1)).put(anyLong(), any());
         then(subscriptionService).shouldHaveNoMoreInteractions();
      }

      @ParameterizedTest
      @MethodSource("renewExceptions")
      @DisplayName("PUT[409 | 404]: Cannot renew.")
      void renewNotAllowed(Class<? extends Throwable> exception, int code) throws Exception {
         given(subscriptionService.put(anyLong(), any()))
            .willThrow(exception);
         mockMvc.perform(put(URI_TEMPLATE + '/' + 1L + "/renew")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().is(code))
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(code));
         then(subscriptionService).should(atLeastOnce()).put(anyLong(), any());
         then(subscriptionService).shouldHaveNoMoreInteractions();
      }
   }

   @Nested
   @DisplayName("PATCH")
   class SubscriptionControllerPatchFinalize {
      @Test
      @DisplayName("PATCH[200] OK: Subscription finalized")
      void finalizeSubscription() throws Exception {
         SubscriptionDtoOutput finalized = new SubscriptionDtoOutput(
            1L, customerDtoOutput,
            subscriptionDtoOutput.membership(),
            subscriptionDtoOutput.price(),
            subscriptionDtoOutput.periods(),
            true
         );
         given(subscriptionService.patch(anyLong()))
            .willReturn(finalized);

         mockMvc.perform(patch(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists());
         then(subscriptionService).should(times(1)).patch(anyLong());
      }

      @Test
      @DisplayName("PATCH[404] Not Found: Subscription not found")
      void subscriptionNotFound() throws Exception {
         given(subscriptionService.patch(anyLong()))
            .willThrow(SubscriptionNotFoundException.class);

         mockMvc.perform(patch(URI_TEMPLATE + '/' + 1L)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());
         then(subscriptionService).should(times(1)).patch(anyLong());
      }
   }

   @Nested
   @DisplayName("DELETE Subscription Resources.")
   class SubscriptionControllerDeleteResources {

      @Test
      @DisplayName("DELETE[204] No Content: Subscription Deleted")
      void deleteSubscription() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + '/' + 1L))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());
         then(subscriptionService).should(times(1)).softDelete(anyLong());
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
         then(subscriptionService).shouldHaveNoInteractions();
      }

   }
}
