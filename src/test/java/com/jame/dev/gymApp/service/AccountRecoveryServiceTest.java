package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.out.AccountRecoveryServiceImplementation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountRecoveryServiceTest {

   @Mock
   VerificationRepository verificationRepository;
   @Mock
   PasswordEncoder passwordEncoder;
   @Mock
   UserRepository userRepository;
   @Mock
   CustomerRepository customerRepository;
   @Mock
   RoleMapper roleMapper;
   @Mock
   RoleRepository roleRepository;

   @InjectMocks
   AccountRecoveryServiceImplementation accountRecoveryService;

   @Test
   @DisplayName("Should reactivate the account.")
   void reactivateUserAccount() {
      VerificationEntity verificationEntity = mock(VerificationEntity.class);
      UserEntity userEntity = mock(UserEntity.class);
      RoleEntity roleEntity = mock(RoleEntity.class);

      given(verificationRepository.findDeactivatedByUser_Email(anyString()))
              .willReturn(Optional.of(verificationEntity));
      given(passwordEncoder.matches(anyString(), any())).willReturn(true);
      given(userRepository.findByEmail(anyString()))
              .willReturn(Optional.of(userEntity));
      given(roleMapper.toEntitySet(any(), any()))
              .willReturn(Set.of(roleEntity));
      assertDoesNotThrow(
              () -> accountRecoveryService.reActivateUserAccount("any@mail.com", "ABC123")
      );

      then(verificationRepository).should(times(1)).findDeactivatedByUser_Email(anyString());
      then(passwordEncoder).should(times(1)).matches(anyString(), any());
      then(userRepository).should(times(1)).findByEmail(anyString());
      then(roleMapper).should(times(1)).toEntitySet(any(), any());
      verifyNoMoreInteractions(verificationRepository, passwordEncoder, userRepository, roleMapper);
   }

   @Test
   @DisplayName("Should reactivate the customer's account.")
   void reactivateCustomerAccount() {
      VerificationEntity verificationEntity = mock(VerificationEntity.class);
      UserEntity user = mock(UserEntity.class);

      String email = "any@mail.com";

      given(verificationRepository.findDeactivatedByUser_Email(anyString()))
              .willReturn(Optional.of(verificationEntity));
      given(passwordEncoder.matches(anyString(), any())).willReturn(true);
      given(customerRepository.findDeactivatedByUser_email(anyString()))
              .willReturn(Optional.of(new CustomerEntity()));

      assertDoesNotThrow(
              () -> accountRecoveryService.reactivateCustomerAccount(email, "ABC123")
      );

      then(verificationRepository).should(times(1)).findDeactivatedByUser_Email(anyString());
      then(passwordEncoder).should(times(1)).matches(anyString(), any());
      then(customerRepository).should(times(1)).findDeactivatedByUser_email(anyString());
      verifyNoMoreInteractions(verificationRepository, passwordEncoder);
   }


   @Test
   @DisplayName("Should validate if the account exists.")
   void accountExists() {
      given(verificationRepository.existsDeactivatedByUser_Email(anyString()))
              .willReturn(true);

      var result = accountRecoveryService.accountExists("any@mail.com");

      assertTrue(result);

      then(verificationRepository).should(times(1)).existsDeactivatedByUser_Email(anyString());
      verifyNoMoreInteractions(verificationRepository);
      verifyNoMoreInteractions(passwordEncoder);
   }

   @Test
   @DisplayName("Should throws VerificationAttemptFailedException")
   void verificationAttemptFailedException() {
      var verificationEntity = mock(VerificationEntity.class);
      given(verificationEntity.getToken()).willReturn("ABC123");
      given(verificationRepository.findDeactivatedByUser_Email(anyString()))
              .willReturn(Optional.of(verificationEntity));
      given(passwordEncoder.matches(anyString(), any())).willReturn(false);

      assertThrowsExactly(
              VerificationAttemptFailedException.class,
              () -> accountRecoveryService.reActivateUserAccount("any@mail.com", "ABC123"));

      then(verificationRepository).should(times(1))
              .findDeactivatedByUser_Email(anyString());
      then(passwordEncoder).should(times(1))
              .matches(anyString(), any());

      verifyNoMoreInteractions(verificationEntity, passwordEncoder);
   }

   @Test
   @DisplayName("Not Found")
   void verificationNotFound() {
      given(verificationRepository.findDeactivatedByUser_Email(anyString()))
              .willThrow(VerificationNotFoundException.class);

      assertThrowsExactly(
              VerificationNotFoundException.class,
              () -> accountRecoveryService.reActivateUserAccount("any@mail.com", "ABC123")
      );

      then(verificationRepository).should(times(1)).findDeactivatedByUser_Email(anyString());
      verifyNoInteractions(passwordEncoder);
      verifyNoMoreInteractions(verificationRepository);
   }
}
