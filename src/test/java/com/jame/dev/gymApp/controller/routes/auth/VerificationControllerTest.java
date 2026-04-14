package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.GlobalExceptionHandler;
import config.TestConfig;
import config.TestValidationConfig;
import com.jame.dev.gymApp.exception.AlreadyVerifiedException;
import com.jame.dev.gymApp.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.service.in.VerificationService;
import com.jame.dev.gymApp.shared.enums.ErrorCodes;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VerificationController.class,
        excludeFilters = @ComponentScan.
                Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {CustomAuthorizationFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, TestConfig.class, TestValidationConfig.class})
@ImportAutoConfiguration({ValidationAutoConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class VerificationControllerTest {

   @Autowired
   MockMvc mockMvc;

   @Autowired
   VerificationController verificationController;

   @Autowired
   ApiErrorResponseFactory apiErrorResponse;

   @MockitoBean
   VerificationService verificationService;

   final String URI = "/auth/verify";
   final VerificationDto verificationDto = new VerificationDto(
           OffsetDateTime.now(), "verified@mail.com", true, "Verified"
   );

   static Stream<Arguments> verificationControllerExceptions() {
      return Stream.of(
              Arguments.of(VerificationNotFoundException.class, 404, ErrorCodes.NOT_FOUND.getCode()),
              Arguments.of(AlreadyVerifiedException.class, 409, ErrorCodes.VALIDATION.getCode()),
              Arguments.of(VerificationAttemptFailedException.class, 400, ErrorCodes.UPDATE.getCode())
      );
   }

   @Test
   @DisplayName("PATCH[200] OK: Account verified.")
   void verificationSuccess() throws Exception {
      willDoNothing().given(verificationService).verify(anyString(), anyString());

      mockMvc.perform(patch(URI + '/' + verificationDto.email())
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                              {
                                 "token": "ANY_TOKEN"
                              }
                              """))
              .andExpect(jsonPath("$.*").doesNotExist());

      then(verificationService).should(times(1)).verify(anyString(), anyString());
      then(verificationService).shouldHaveNoMoreInteractions();
   }

   @ParameterizedTest
   @MethodSource({"verificationControllerExceptions"})
   @DisplayName("PATCH[404 | 409 | 400].")
   void notFoundAlreadyExistsOrAttemptFailed(
           Class<? extends Throwable> ex,
           int statusCode,
           String code) throws Exception {
       willThrow(ex).given(verificationService).verify(anyString(), anyString());

      mockMvc.perform(patch(URI + '/' + verificationDto.email())
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                              {
                                 "token": "ANY_TOKEN"
                              }
                              """))
              .andExpect(status().is(statusCode))
              .andExpect(jsonPath("$.*").exists())
              .andExpect(jsonPath("$.status").value(statusCode))
              .andExpect(jsonPath("$.code").value(code));

      then(verificationService).should().verify(anyString(), anyString());
      then(verificationService).shouldHaveNoMoreInteractions();
   }
}