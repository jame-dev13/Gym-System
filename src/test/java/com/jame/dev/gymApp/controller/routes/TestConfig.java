package com.jame.dev.gymApp.controller.routes;

import com.jame.dev.gymApp.controller.advice.ApiErrorResponse;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;

@TestConfiguration
public class TestConfig {

   @Bean
   public ApiErrorResponseFactory responseFactory() {
      ApiErrorResponseFactory responseFactory = mock(ApiErrorResponseFactory.class);
      lenient().when(responseFactory.buildErrorResponse(
                      any(Exception.class),
                      any(),
                      any(),
                      any()))
              .thenAnswer(this::defaultErrorAnswer);

      return responseFactory;
   }

   private ApiErrorResponse defaultErrorAnswer(InvocationOnMock invocation) {
      Exception ex = invocation.getArgument(0);
      HttpServletRequest req = invocation.getArgument(1);
      HttpStatus status = invocation.getArgument(2);
      String code = invocation.getArgument(3);

      return ApiErrorResponse.builder()
              .timestamp(OffsetDateTime.now())
              .status(status.value())
              .error(status.getReasonPhrase())
              .message(ex != null ? ex.getMessage() : "Error")
              .path(req != null ? req.getRequestURI() : "/unknown")
              .code(code)
              .build();
   }
}
