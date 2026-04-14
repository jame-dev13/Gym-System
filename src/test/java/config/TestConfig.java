package config;

import com.jame.dev.gymApp.controller.advice.ApiErrorResponse;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.advice.InputError;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

   @Bean
   public static ApiErrorResponseFactory responseFactory() {
      ApiErrorResponseFactory responseFactory = mock(ApiErrorResponseFactory.class);
      lenient().when(responseFactory.buildResponse(any(InputError.class)))
              .thenAnswer(TestConfig::defaultErrorAnswer);

      return responseFactory;
   }

   private static ResponseEntity<ApiErrorResponse> defaultErrorAnswer(InvocationOnMock invocation) {
      InputError input = invocation.getArgument(0);

      ApiErrorResponse errorBody = ApiErrorResponse.builder()
              .timestamp(OffsetDateTime.now())
              .status(input != null && input.httpStatusCode() != null ? input.httpStatusCode().value() : 500)
              .error(input != null && input.httpStatusCode() != null ? input.httpStatusCode().getReasonPhrase() : "Error")
              .message(input != null && input.ex() != null ? input.ex().getMessage() : "Mock Error")
              .path(input != null && input.request() != null ? input.request().getRequestURI() : "/unknown")
              .code(input != null ? input.errorCode().getCode(): "unknown")
              .build();
      return ResponseEntity
              .status(errorBody.status())
              .body(errorBody);
   }
}