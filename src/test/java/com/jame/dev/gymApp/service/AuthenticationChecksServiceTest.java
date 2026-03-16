package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.repository.AuthenticationChecksQueriesRepository;
import com.jame.dev.gymApp.service.out.AuthenticationChecksServiceImp;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationChecksServiceTest {

   @Mock
   AuthenticationChecksQueriesRepository queriesRepository;

   @InjectMocks
   AuthenticationChecksServiceImp authenticationChecksServiceImp;

   @Test
   @DisplayName("Should validate if is local provider.")
   void isLocalProvider() {
      given(queriesRepository.isLocalProvider(anyString())).willReturn(true);

      boolean result = authenticationChecksServiceImp.isLocalProvider("any@mail.com");

      assertTrue(result);

      verify(queriesRepository, times(1)).isLocalProvider(anyString());
      verifyNoMoreInteractions(queriesRepository);
   }

   @Test
   @DisplayName("Should validate if is local provider.")
   void isNotLocalProvider() {
      given(queriesRepository.isLocalProvider(anyString())).willReturn(false);

      boolean result = authenticationChecksServiceImp.isLocalProvider("any@mail.com");

      assertFalse(result);

      verify(queriesRepository, times(1)).isLocalProvider(anyString());
      verifyNoMoreInteractions(queriesRepository);
   }
}
