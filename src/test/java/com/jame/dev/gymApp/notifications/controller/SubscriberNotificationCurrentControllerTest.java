package com.jame.dev.gymApp.notifications.controller;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.notification.api.SubscriberNotificationController;
import com.jame.dev.gymApp.features.notification.api.request.DayRangeRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.ActivateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.ChangeDayRangeSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.DeactivateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.query.GetCurrentSubscriberNotificationUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = SubscriberNotificationController.class,
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
class SubscriberNotificationCurrentControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private GetCurrentSubscriberNotificationUseCase getCurrent;

   @MockitoBean
   private ActivateSubscriberNotificationUseCase activate;

   @MockitoBean
   private DeactivateSubscriberNotificationUseCase deactivate;

   @MockitoBean
   private ChangeDayRangeSubscriberNotificationUseCase changeDayRange;

   private final String URI_TEMPLATE = "/app/v1/notifications/current";

   private final SubscriberNotificationResponse response =
      new SubscriberNotificationResponse(7, "2026-09-01T12:00:00", true);

   @Nested
   @DisplayName("GET /app/v1/notifications/current")
   class GetCurrentNotificationTests {

      @Test
      @DisplayName("GET[200] OK: current subscriber notification returned with its full body")
      void getCurrent() throws Exception {
         given(getCurrent.getCurrent(any())).willReturn(response);

         mockMvc.perform(get(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.rangeDaysNotification").value(7),
               jsonPath("$.nextNotificationDate").value("2026-09-01T12:00:00"),
               jsonPath("$.notifiable").value(true));

         verify(getCurrent, times(1)).getCurrent(any());
         verifyNoMoreInteractions(getCurrent);
         verifyNoInteractions(activate, deactivate, changeDayRange);
      }

      @Test
      @DisplayName("GET[404] Not Found: no notification record for the current user")
      void getCurrentNotFound() throws Exception {
         willThrow(new NotFoundException("Notification record not found for: user@mail.com"))
            .given(getCurrent).getCurrent(any());

         mockMvc.perform(get(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.status").value(404),
               jsonPath("$.code").value("NOT_FOUND_OPERATION"));

         verify(getCurrent, times(1)).getCurrent(any());
         verifyNoMoreInteractions(getCurrent);
         verifyNoInteractions(activate, deactivate, changeDayRange);
      }
   }

   @Nested
   @DisplayName("PATCH /app/v1/notifications/current")
   class PatchNotificationsTests {

      @Test
      @DisplayName("PATCH[200] OK: notifications activated")
      void activate() throws Exception {
         given(activate.activateNotification(any())).willReturn(response);

         mockMvc.perform(patch(URI_TEMPLATE + "/activate")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.rangeDaysNotification").value(7),
               jsonPath("$.notifiable").value(true));

         verify(activate, times(1)).activateNotification(any());
         verifyNoMoreInteractions(activate);
         verifyNoInteractions(getCurrent, deactivate, changeDayRange);
      }

      @Test
      @DisplayName("PATCH[404] Not Found: activation when no notification record exists")
      void activateNotFound() throws Exception {
         willThrow(new NotFoundException("Notification record not found for: user@mail.com"))
            .given(activate).activateNotification(any());

         mockMvc.perform(patch(URI_TEMPLATE + "/activate")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.status").value(404),
               jsonPath("$.code").value("NOT_FOUND_OPERATION"));

         verify(activate, times(1)).activateNotification(any());
         verifyNoInteractions(getCurrent, deactivate, changeDayRange);
      }

      @Test
      @DisplayName("PATCH[200] OK: notifications deactivated")
      void deactivate() throws Exception {
         final var deactivated =
            new SubscriberNotificationResponse(7, "2026-09-01T12:00:00", false);
         given(deactivate.deactivateNotification(any())).willReturn(deactivated);

         mockMvc.perform(patch(URI_TEMPLATE + "/deactivate")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.rangeDaysNotification").value(7),
               jsonPath("$.notifiable").value(false));

         verify(deactivate, times(1)).deactivateNotification(any());
         verifyNoMoreInteractions(deactivate);
         verifyNoInteractions(getCurrent, activate, changeDayRange);
      }

      @Test
      @DisplayName("PATCH[404] Not Found: deactivation when no notification record exists")
      void deactivateNotFound() throws Exception {
         willThrow(new NotFoundException("Notification record not found for: user@mail.com"))
            .given(deactivate).deactivateNotification(any());

         mockMvc.perform(patch(URI_TEMPLATE + "/deactivate")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.status").value(404),
               jsonPath("$.code").value("NOT_FOUND_OPERATION"));

         verify(deactivate, times(1)).deactivateNotification(any());
         verifyNoInteractions(getCurrent, activate, changeDayRange);
      }

      @Test
      @DisplayName("PATCH[200] OK: day range updated")
      void changeDayRange() throws Exception {
         final String payload = """
            {
               "numberOfDays": 5
            }
            """;
         given(changeDayRange.changeDayRange(any(), any(DayRangeRequest.class))).willReturn(response);

         mockMvc.perform(patch(URI_TEMPLATE + "/range")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.rangeDaysNotification").value(7),
               jsonPath("$.nextNotificationDate").value("2026-09-01T12:00:00"));

         verify(changeDayRange, times(1)).changeDayRange(any(), any(DayRangeRequest.class));
         verifyNoMoreInteractions(changeDayRange);
         verifyNoInteractions(getCurrent, activate, deactivate);
      }

      @Test
      @DisplayName("PATCH[404] Not Found: range update when no notification record exists")
      void changeDayRangeNotFound() throws Exception {
         final String payload = """
            {
               "numberOfDays": 5
            }
            """;
         willThrow(new NotFoundException("Notification record not found for: user@mail.com"))
            .given(changeDayRange).changeDayRange(any(), any(DayRangeRequest.class));

         mockMvc.perform(patch(URI_TEMPLATE + "/range")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.status").value(404),
               jsonPath("$.code").value("NOT_FOUND_OPERATION"));

         verify(changeDayRange, times(1)).changeDayRange(any(), any(DayRangeRequest.class));
         verifyNoInteractions(getCurrent, activate, deactivate);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true, textBlock = """
         PAYLOAD,                    ERROR_CODE
         '{"numberOfDays":2}',       VALIDATION_OPERATION
         '{"numberOfDays":8}',       VALIDATION_OPERATION
         '{}',                       VALIDATION_OPERATION
         '{"numberOfDays":null}',    VALIDATION_OPERATION
         """)
      @DisplayName("PATCH[400] Bad Request: numberOfDays out of bounds or missing/null")
      void badRequestInvalidNumberOfDays(String payload, String expectedCode) throws Exception {
         mockMvc.perform(patch(URI_TEMPLATE + "/range")
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.status").value(400),
               jsonPath("$.code").value(expectedCode));

         verifyNoInteractions(getCurrent, activate, deactivate, changeDayRange);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = TestDataSource.BODY_FORMAT_ERRORS,
         nullValues = "NULL",
         emptyValue = "EMPTY")
      @DisplayName("PATCH[400] Bad Request: malformed request body")
      void badRequestMalformedBody(String value, String codeExpected) throws Exception {
         mockMvc.perform(patch(URI_TEMPLATE + "/range")
               .contentType(MediaType.APPLICATION_JSON)
               .content(value))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.status").value(400),
               jsonPath("$.code").value(codeExpected));

         verifyNoInteractions(getCurrent, activate, deactivate, changeDayRange);
      }
   }
}
