package com.jame.dev.gymApp.notification.controller;

import com.jame.dev.gymApp.features.notification.api.SubscriberNotificationController;
import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationRequest;
import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationUpdateRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.CreateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.DeleteSubscriberNotificationById;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.UpdateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.query.GetByIdSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.presentation.exception.GlobalExceptionHandler;
import config.TestConfig;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
   controllers = SubscriberNotificationController.class,
   excludeFilters = {
      @ComponentScan.Filter(
         type = FilterType.ASSIGNABLE_TYPE,
         classes = com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter.class
      )}
)
@AutoConfigureMockMvc(addFilters = false)
@Import({
   GlobalExceptionHandler.class,
   TestValidationConfig.class,
   TestConfig.class})
@ImportAutoConfiguration({ValidationAutoConfiguration.class})
class SubscriberNotificationControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private GetByIdSubscriberNotificationUseCase getById;

   @MockitoBean
   private CreateSubscriberNotificationUseCase create;

   @MockitoBean
   private UpdateSubscriberNotificationUseCase update;

   @MockitoBean
   private DeleteSubscriberNotificationById delete;

   private final String URI_TEMPLATE = "/app/v1/notifications";

   private final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");

   private final SubscriberNotificationResponse response = new SubscriberNotificationResponse(
      uuid, 3, "2026-01-01T00:00:00", null
   );

   @Nested
   @DisplayName("GET: /app/v1/notifications/{id}")
   class GetSubscriberNotificationTests {

      @Test
      @DisplayName("GET[200] OK: /notifications/{uuid}")
      void getById() throws Exception {
         given(getById.getById(uuid)).willReturn(response);

         mockMvc.perform(get(URI_TEMPLATE + "/" + uuid)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid").value(uuid.toString()));

         then(getById).should(times(1)).getById(uuid);
      }

      @Test
      @DisplayName("GET[404] Not Found: /notifications/{uuid}")
      void getByIdNotFound() throws Exception {
         given(getById.getById(uuid)).willThrow(NotificationException.class);

         mockMvc.perform(get(URI_TEMPLATE + "/" + uuid)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists());

         then(getById).should(times(1)).getById(uuid);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true, textBlock = """
              VALUE,    ERROR_CODE
              0,        TYPE_MISMATCH
              -100,     TYPE_MISMATCH
              letters,  TYPE_MISMATCH
              NULL,     TYPE_MISMATCH
              """)
      @DisplayName("GET[400] Bad Request: /notifications/{invalidId}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(get(URI_TEMPLATE + "/" + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));

         then(getById).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("POST: /app/v1/notifications")
   class PostSubscriberNotificationTests {

      private final String payload = """
         {
            "subscriptionId": 1,
            "rangeDays": 3
         }
         """;

      @Test
      @DisplayName("POST[201] Created")
      void create() throws Exception {
         given(create.createSubscriberNotification(any(SubscriberNotificationRequest.class)))
            .willReturn(response);

         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/" + uuid)))
            .andExpect(jsonPath("$.uuid").value(uuid.toString()));

         then(create).should(times(1)).createSubscriberNotification(any(SubscriberNotificationRequest.class));
      }

      @Test
      @DisplayName("POST[404] Not Found: Subscription does not exist")
      void createSubscriptionNotFound() throws Exception {
         given(create.createSubscriberNotification(any(SubscriberNotificationRequest.class)))
            .willThrow(SubscriptionNotFoundException.class);

         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));

         then(create).should(times(1)).createSubscriberNotification(any(SubscriberNotificationRequest.class));
      }

      @Test
      @DisplayName("POST[409] Conflict: Notification already exists")
      void createAlreadyExists() throws Exception {
         given(create.createSubscriberNotification(any(SubscriberNotificationRequest.class)))
            .willThrow(NotificationException.class);

         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409));

         then(create).should(times(1)).createSubscriberNotification(any(SubscriberNotificationRequest.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true, textBlock = """
              VALUE,  ERROR_CODE
              {NULL},   VALIDATION_OPERATION
              {EMPTY},  VALIDATION_OPERATION
              """)
      @DisplayName("POST[400] Bad Request: Invalid payload format")
      void badRequestInvalidPayload(String value, String codeExpected) throws Exception {
         mockMvc.perform(post(URI_TEMPLATE)
               .contentType(MediaType.APPLICATION_JSON)
               .content(value))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));

         then(create).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("PATCH: /app/v1/notifications/{id}")
   class PatchSubscriberNotificationTests {

      private final String payload = """
         {
            "rangeDays": 5
         }
         """;

      @Test
      @DisplayName("PATCH[200] OK: Update range days")
      void patchRangeDays() throws Exception {
         given(update.updateSubscriberNotification(
            any(UUID.class), any(SubscriberNotificationUpdateRequest.class)))
            .willReturn(response);

         mockMvc.perform(patch(URI_TEMPLATE + "/" + uuid)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid").value(uuid.toString()));

         then(update).should(times(1))
            .updateSubscriberNotification(any(UUID.class), any(SubscriberNotificationUpdateRequest.class));
      }

      @Test
      @DisplayName("PATCH[404] Not Found: Notification not found")
      void patchRangeDaysNotFound() throws Exception {
         given(update.updateSubscriberNotification(
            any(UUID.class), any(SubscriberNotificationUpdateRequest.class)))
            .willThrow(NotificationException.class);

         mockMvc.perform(patch(URI_TEMPLATE + "/" + uuid)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));

         then(update).should(times(1))
            .updateSubscriberNotification(any(UUID.class), any(SubscriberNotificationUpdateRequest.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true, textBlock = """
              VALUE,    ERROR_CODE
              0,        TYPE_MISMATCH
              -100,     TYPE_MISMATCH
              letters,  TYPE_MISMATCH
              NULL,     TYPE_MISMATCH
              """)
      @DisplayName("PATCH[400] Bad Request: /notifications/{invalidId}")
      void patchInvalidIdPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(patch(URI_TEMPLATE + "/" + value)
               .contentType(MediaType.APPLICATION_JSON)
               .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));

         then(update).shouldHaveNoInteractions();
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true, textBlock = """
              VALUE,  ERROR_CODE
              {NULL},   VALIDATION_OPERATION
              {EMPTY},  VALIDATION_OPERATION
              """)
      @DisplayName("PATCH[400] Bad Request: Invalid payload format")
      void patchInvalidPayload(String value, String codeExpected) throws Exception {
         mockMvc.perform(patch(URI_TEMPLATE + "/" + uuid)
               .contentType(MediaType.APPLICATION_JSON)
               .content(value))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(codeExpected));

         then(update).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("DELETE: /app/v1/notifications/{id}")
   class DeleteSubscriberNotificationTests {

      @Test
      @DisplayName("DELETE[204] No Content")
      void deleteNotification() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + "/" + uuid))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.*").doesNotExist());

         then(delete).should(times(1)).deleteSubscriberNotificationById(uuid);
      }

      @Test
      @DisplayName("DELETE[404] Not Found: Notification not found")
      void deleteNotificationNotFound() throws Exception {
         willThrow(NotificationException.class)
            .given(delete).deleteSubscriberNotificationById(uuid);

         mockMvc.perform(delete(URI_TEMPLATE + "/" + uuid))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));

         then(delete).should(times(1)).deleteSubscriberNotificationById(uuid);
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true, textBlock = """
              VALUE,    ERROR_CODE
              0,        TYPE_MISMATCH
              -100,     TYPE_MISMATCH
              letters,  TYPE_MISMATCH
              NULL,     TYPE_MISMATCH
              """)
      @DisplayName("DELETE[400] Bad Request: /notifications/{invalidId}")
      void deleteInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + "/" + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));

         then(delete).shouldHaveNoInteractions();
      }
   }
}
