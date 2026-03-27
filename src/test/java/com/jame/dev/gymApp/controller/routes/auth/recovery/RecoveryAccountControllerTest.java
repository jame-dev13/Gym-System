package com.jame.dev.gymApp.controller.routes.auth.recovery;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.GlobalExceptionHandler;
import com.jame.dev.gymApp.controller.routes.TestConfig;
import com.jame.dev.gymApp.controller.routes.TestDataSource;
import com.jame.dev.gymApp.controller.routes.TestValidationConfig;
import com.jame.dev.gymApp.controller.routes.auth.RecoveryAccountController;
import com.jame.dev.gymApp.service.in.AccountRecoveryService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = RecoveryAccountController.class,
        excludeFilters = {@ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = CustomAuthorizationFilter.class)}
)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GlobalExceptionHandler.class,
        TestConfig.class,
        TestValidationConfig.class
})
@ImportAutoConfiguration({ValidationAutoConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecoveryAccountControllerTest {

   @Autowired
   MockMvc mockMvc;

   @Autowired
   RecoveryAccountController recoveryAccountController;

   @Autowired
   ApiErrorResponseFactory responseFactory;

   @MockitoBean
   AccountRecoveryService accountRecoveryService;

   final String URI = "/auth/accounts";

   @Test
   @DisplayName("POST[202] Created: Should publish the event")
   void publishEvent() throws Exception {
      mockMvc.perform(post(URI + "/recover")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                              {
                               "email": "any@mail.com"
                              }
                              """))
              .andExpect(status().isAccepted())
              .andExpect(jsonPath("$.*").doesNotExist());

      verifyNoInteractions(accountRecoveryService);
   }

   @Test
   @DisplayName("POST[200] Ok: Reactivate user's account")
   void reactivateAccount() throws Exception {
      willDoNothing().given(accountRecoveryService).reActivateUserAccount(anyString(), anyString());

      mockMvc.perform(post(URI + "/activate")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                              {
                                 "email": "email@mail.com",
                                 "token": "token"
                              }
                              """)
              )
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.*").doesNotExist());
      then(accountRecoveryService).should(times(1)).reActivateUserAccount(anyString(), anyString());
      verifyNoMoreInteractions(accountRecoveryService);
   }

   @ParameterizedTest
   @CsvSource(
           useHeadersInDisplayName = true,
           textBlock = TestDataSource.PAYLOAD_VALIDATIONS_ERRORS,
           nullValues = "NULL",
           emptyValue = "EMPTY")
   @DisplayName("POST[400] Bad Request due to constraint validations.")
   void endpointForReActivateThrows(String email, String codeError) throws Exception {
      mockMvc.perform(post(URI + "/activate")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                              {
                                 "email": "%s",
                                 "token": "token"
                              }
                              """.formatted(email)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.*").exists())
              .andExpect(jsonPath("$.code").value(codeError));
   }
}
