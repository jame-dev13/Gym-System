package com.jame.dev.gymApp.controller.routes.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.auth.service.AuthService;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VerificationController.class,
        excludeFilters = @ComponentScan.
                Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {CustomAuthorizationFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
class VerificationControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private VerificationController controller;

   @MockitoBean
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private AuthService authService;

   private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

   @Test
   @DisplayName("[PATCH]: Should verify the account.")
   void verifyAccount() throws Exception {
      final String email = "any@mail.com";
      final String token = "ABC123";
      final VerificationDto verificationDto = VerificationDto.builder()
              .timestamp(OffsetDateTime.now())
              .email(email)
              .verified(true)
              .msg("Verified")
              .build();
      when(authService.verify(anyString(), anyString())).thenReturn(Optional.of(verificationDto));

      final String requestBody = mapper.writeValueAsString(new VerificationRequest(token));
      final String jsonExpected = mapper.writeValueAsString(
              VerificationDto.builder()
                      .timestamp(OffsetDateTime.now())
                      .email(email)
                      .verified(true)
                      .msg("verified")
                      .build()
      );

      mockMvc.perform(patch("/auth/verify/{email}", "any@mail.com")
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content("""
                                  {
                                    "token": "any-token"
                                  }
                              """))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.email").value("any@mail.com"))
              .andExpect(jsonPath("$.isVerified").value(true))
              .andExpect(jsonPath("$.msg").value("Verified"))
              .andExpect(jsonPath("$.timestamp").exists());

      verify(authService, times(1)).verify(anyString(), anyString());
      verifyNoMoreInteractions(authService);
   }
}