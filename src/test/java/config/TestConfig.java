package config;

import com.jame.dev.gymApp.presentation.exception.ApiErrorResponse;
import com.jame.dev.gymApp.presentation.exception.ApiErrorResponseFactory;
import com.jame.dev.gymApp.presentation.exception.ApiErrorKind;
import jakarta.servlet.http.HttpServletRequest;
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
      lenient().when(responseFactory.of(any(ApiErrorKind.class), any(Throwable.class), any(HttpServletRequest.class)))
              .thenAnswer(TestConfig::defaultErrorAnswer);

      return responseFactory;
   }

   private static ResponseEntity<ApiErrorResponse> defaultErrorAnswer(InvocationOnMock invocation) {
      ApiErrorKind kind = invocation.getArgument(0);
      Throwable ex = invocation.getArgument(1);
      HttpServletRequest request = invocation.getArgument(2);

      ApiErrorResponse errorBody = ApiErrorResponse.builder()
              .timestamp(OffsetDateTime.now())
              .status(kind != null ? kind.getStatus().value() : 500)
              .error(kind != null ? kind.getStatus().getReasonPhrase() : "Error")
              .message(ex != null ? ex.getMessage() : "Mock Error")
              .path(request != null ? request.getRequestURI() : "/unknown")
              .code(kind != null && kind.getCode() != null ? kind.getCode().getCode() : "unknown")
              .build();
      return ResponseEntity
              .status(errorBody.status())
              .body(errorBody);
   }
}
