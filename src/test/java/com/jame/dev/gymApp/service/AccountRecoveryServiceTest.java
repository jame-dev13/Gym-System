package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerRepository;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.RoleRepository;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.auth.application.service.AccountRecoveryApplicationService;
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
   AccountRecoveryApplicationService accountRecoveryService;

   @Test
   @DisplayName("Should reactivate the account of customer and user type.")
   void reactivateBothAccounts() {
      VerificationEntity verificationEntity = mock(VerificationEntity.class);
      UserEntity userEntity = mock(UserEntity.class);
      CustomerEntity customerEntity = mock(CustomerEntity.class);
      RoleEntity roleEntity = mock(RoleEntity.class);

      given(verificationRepository.findDeactivatedByUser_Email(anyString()))
              .willReturn(Optional.of(verificationEntity));
      given(passwordEncoder.matches(anyString(), any())).willReturn(true);
      given(userRepository.findByEmail(anyString()))
              .willReturn(Optional.of(userEntity));
      given(roleMapper.toEntitySet(any(), any()))
              .willReturn(Set.of(roleEntity));
      given(customerRepository.existsDeactivatedByUserId(anyLong())).willReturn(true);
      given(customerRepository.findDeactivatedByUserId(anyLong()))
              .willReturn(Optional.of(customerEntity));
      assertDoesNotThrow(
              () -> accountRecoveryService.reActivateUserAccount("any@mail.com", "ABC123")
      );

      then(verificationRepository).should(times(1)).findDeactivatedByUser_Email(anyString());
      then(passwordEncoder).should(times(1)).matches(anyString(), any());
      then(userRepository).should(times(1)).findByEmail(anyString());
      then(roleMapper).should(times(1)).toEntitySet(any(), any());
      then(customerRepository).should(times(1)).existsDeactivatedByUserId(anyLong());
      then(customerRepository).should(times(1)).findDeactivatedByUserId(anyLong());
      verifyNoMoreInteractions(verificationRepository, passwordEncoder, userRepository, roleMapper, customerRepository);
   }

   @Test
   @DisplayName("Should reactivate only the user account.")
   void reactivateUserAccount() {
      VerificationEntity verificationEntity = mock(VerificationEntity.class);
      UserEntity user = mock(UserEntity.class);

      String email = "any@mail.com";

      given(verificationRepository.findDeactivatedByUser_Email(anyString()))
              .willReturn(Optional.of(verificationEntity));
      given(passwordEncoder.matches(anyString(), any())).willReturn(true);
      given(userRepository.findByEmail(anyString()))
              .willReturn(Optional.of(user));
      given(customerRepository.existsDeactivatedByUserId(anyLong())).willReturn(false);

      assertDoesNotThrow(
              () -> accountRecoveryService.reActivateUserAccount(email, "ABC123")
      );

      then(verificationRepository).should(times(1)).findDeactivatedByUser_Email(anyString());
      then(passwordEncoder).should(times(1)).matches(anyString(), any());
      then(userRepository).should(times(1)).findByEmail(anyString());
      then(customerRepository).should(times(1)).existsDeactivatedByUserId(anyLong());
      verifyNoMoreInteractions(verificationRepository, passwordEncoder, userRepository, customerRepository);
   }

   @Test
   @DisplayName("Should reactivate only the customer account.")
   void reactivateCustomerAccount() {
      VerificationEntity verificationEntity = mock(VerificationEntity.class);
      UserEntity user = mock(UserEntity.class);
      CustomerEntity customer = mock(CustomerEntity.class);

      given(verificationRepository.findDeactivatedByUser_Email(anyString()))
              .willReturn(Optional.of(verificationEntity));
      given(passwordEncoder.matches(anyString(), any()))
              .willReturn(true);
      given(userRepository.findByEmail(anyString()))
              .willReturn(Optional.of(user));
      given(user.isActive()).willReturn(true);
      given(customerRepository.existsDeactivatedByUserId(anyLong()))
              .willReturn(true);
      given(customerRepository.findDeactivatedByUserId(anyLong()))
              .willReturn(Optional.of(customer));

      assertDoesNotThrow(() -> accountRecoveryService.reActivateUserAccount("any@mail.com", "TOKEN"));

      then(verificationRepository).should(times(1)).findDeactivatedByUser_Email(anyString());
      then(passwordEncoder).should(times(1)).matches(anyString(), any());
      then(userRepository).should(times(1)).findByEmail(anyString());
      then(customerRepository).should(times(1)).existsDeactivatedByUserId(anyLong());
      then(customerRepository).should(times(1)).findDeactivatedByUserId(anyLong());

      verifyNoMoreInteractions(userRepository, customerRepository);
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
