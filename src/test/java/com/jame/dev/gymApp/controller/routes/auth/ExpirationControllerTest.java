package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.GlobalExceptionHandler;
import com.jame.dev.gymApp.controller.routes.TestConfig;
import com.jame.dev.gymApp.controller.routes.TestValidationConfig;
import com.jame.dev.gymApp.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.exception.WindowTimeException;
import com.jame.dev.gymApp.service.in.ExpirationService;
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

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpirationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = CustomAuthorizationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, TestConfig.class, TestValidationConfig.class})
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpirationControllerTest {

   @Autowired
   MockMvc mockMvc;

   @Autowired
   ExpirationController expirationController;

   @Autowired
   ApiErrorResponseFactory responseFactory;

   @MockitoBean
   ExpirationService expirationService;

   final String URI = "/auth/expiration";

   static Stream<Arguments> expirationArguments() {
      return Stream.of(
              Arguments.of(VerificationNotFoundException.class, 404),
              Arguments.of(WindowTimeException.class, 400)
      );
   }

   @Test
   @DisplayName("PATCH[200]: Expiration request ok")
   void expirationUpdated() throws Exception {
      mockMvc.perform(patch(URI + "/someone@mail.com" + "/refresh"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.*").doesNotExist());

      then(expirationService).should(times(1)).getMoreTimeFor(anyString());
      verifyNoMoreInteractions(expirationService);
   }

   @ParameterizedTest
   @MethodSource("expirationArguments")
   @DisplayName("PATCH[404 | 400]: returns not found or Bad Request")
   void notFoundOrBadRequest(Class<? extends Throwable> ex, int codeExpected) throws Exception {
      willThrow(ex).given(expirationService).getMoreTimeFor(anyString());
      mockMvc.perform(patch(URI + "/someone@mail.com" + "/refresh")
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().is(codeExpected))
              .andExpect(jsonPath("$.*").exists())
              .andExpect(jsonPath("$.status").value(codeExpected));

      then(expirationService).should(atLeastOnce()).getMoreTimeFor(anyString());
   }
}
