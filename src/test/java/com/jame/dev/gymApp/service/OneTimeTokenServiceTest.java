package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.OneTimeTokenEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.ExpiredException;
import com.jame.dev.gymApp.exception.MissMatchException;
import com.jame.dev.gymApp.exception.OTTNotFoundException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.model.dto.auth.TokenIdResetPasswordRequest;
import com.jame.dev.gymApp.model.dto.in.PasswordResetDtoInput;
import com.jame.dev.gymApp.repository.OneTimeTokenRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.TokenDBHasherService;
import com.jame.dev.gymApp.service.out.OneTimeTokenServiceImp;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OneTimeTokenServiceTest {

    @Mock
    private OneTimeTokenRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenDBHasherService hasherService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OneTimeTokenServiceImp underTest;

    @Nested
    @DisplayName("Tests for saveToken method")
    class SaveTokenTest {

        @Test
        @DisplayName("Should save token successfully with valid inputs")
        void shouldSaveTokenSuccessfully() {
            String rawToken = "validRawToken";
            UserEntity user = mock(UserEntity.class);
            when(user.getId()).thenReturn(1L);
            String hashedToken = "hashedToken";

            given(hasherService.hashToken(rawToken)).willReturn(hashedToken);

            underTest.saveToken(rawToken, user);

            verify(repository).deleteByUserId(eq(1L));
            verify(hasherService).hashToken(eq(rawToken));

            ArgumentCaptor<OneTimeTokenEntity> captor = ArgumentCaptor.forClass(OneTimeTokenEntity.class);
            verify(repository).saveAndFlush(captor.capture());

            OneTimeTokenEntity saved = captor.getValue();
            assertEquals(user, saved.getUser());
            assertEquals(hashedToken, saved.getHashToken());
            assertTrue(saved.getExpiresAt().isAfter(Instant.now()));
        }
    }

    @Nested
    @DisplayName("Tests for validateTokenRequest method")
    class ValidateTokenRequestTest {

        @Test
        @DisplayName("Should pass validation with valid non-expired matching token")
        void shouldValidateSuccessfully() {
            long userId = 1L;
            String rawToken = "validRawToken";
            String hashedToken = "hashedToken";
            TokenIdResetPasswordRequest request = new TokenIdResetPasswordRequest(rawToken, userId);

            OneTimeTokenEntity tokenEntity = mock(OneTimeTokenEntity.class);
            when(tokenEntity.getHashToken()).thenReturn(hashedToken);
            when(tokenEntity.getExpiresAt()).thenReturn(Instant.now().plusSeconds(300L));

            given(repository.findByUserId(userId)).willReturn(Optional.of(tokenEntity));
            given(hasherService.tokenMatches(rawToken, hashedToken)).willReturn(true);

            assertDoesNotThrow(() -> underTest.validateTokenRequest(request));

            verify(repository).findByUserId(eq(userId));
            verify(hasherService).tokenMatches(eq(rawToken), eq(hashedToken));
        }

        @Test
        @DisplayName("Should throw OTTNotFoundException when token not found")
        void shouldThrowNotFoundWhenTokenMissing() {
            TokenIdResetPasswordRequest request = new TokenIdResetPasswordRequest("rawToken", 1L);
            willThrow(OTTNotFoundException.class)
               .given(repository).findByUserId(1L);

            assertThrowsExactly(OTTNotFoundException.class, () -> underTest.validateTokenRequest(request));
            verify(repository).findByUserId(eq(1L));
        }

        @Test
        @DisplayName("Should throw ExpiredException when token is expired")
        void shouldThrowExpiredWhenTokenExpired() {
            Long userId = 1L;
            TokenIdResetPasswordRequest request = new TokenIdResetPasswordRequest("rawToken", userId);

            OneTimeTokenEntity tokenEntity = mock(OneTimeTokenEntity.class);
            when(tokenEntity.getExpiresAt()).thenReturn(Instant.now().minusSeconds(10L));

            given(repository.findByUserId(userId)).willReturn(Optional.of(tokenEntity));

            assertThrowsExactly(ExpiredException.class, () -> underTest.validateTokenRequest(request));
            verify(repository).findByUserId(eq(userId));
            verify(hasherService, never()).tokenMatches(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw MissMatchException when token hash mismatch")
        void shouldThrowMismatchWhenHashInvalid() {
            Long userId = 1L;
            String rawToken = "rawToken";
            String hashedToken = "hashedToken";
            TokenIdResetPasswordRequest request = new TokenIdResetPasswordRequest(rawToken, userId);

            OneTimeTokenEntity tokenEntity = mock(OneTimeTokenEntity.class);
            when(tokenEntity.getHashToken()).thenReturn(hashedToken);
            when(tokenEntity.getExpiresAt()).thenReturn(Instant.now().plusSeconds(300L));

            given(repository.findByUserId(userId)).willReturn(Optional.of(tokenEntity));
            given(hasherService.tokenMatches(rawToken, hashedToken)).willReturn(false);

            assertThrowsExactly(MissMatchException.class, () -> underTest.validateTokenRequest(request));
            verify(repository).findByUserId(eq(userId));
            verify(hasherService).tokenMatches(eq(rawToken), eq(hashedToken));
        }
    }

    @Nested
    @DisplayName("Tests for resetPassword method")
    class ResetPasswordTest {

        @Test
        @DisplayName("Should reset password successfully with valid inputs")
        void shouldResetPasswordSuccessfully() {
            String email = "test@example.com";
            String newPassword = "newPassword123";
            String hashedNewPassword = "hashedNewPassword";
            PasswordResetDtoInput request = new PasswordResetDtoInput(email, newPassword);

            UserEntity user = mock(UserEntity.class);
            when(user.getId()).thenReturn(1L);

            given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
            given(repository.existsByUserId(1L)).willReturn(true);
            given(passwordEncoder.encode(newPassword)).willReturn(hashedNewPassword);

            underTest.resetPassword(request);

            verify(userRepository).findByEmail(eq(email));
            verify(repository).existsByUserId(eq(1L));
            verify(passwordEncoder).encode(eq(newPassword));
            verify(user).setPassword(hashedNewPassword);
            verify(repository).deleteByUserId(eq(1L));
        }

        @Test
        @DisplayName("Should throw UserEntityNotFoundException when email not found")
        void shouldThrowUserNotFoundWhenEmailInvalid() {
            PasswordResetDtoInput request = new PasswordResetDtoInput("nonexistent@example.com", "newPass");
            willThrow(UserEntityNotFoundException.class)
               .given(userRepository)
               .findByEmail(request.email());

            assertThrowsExactly(UserEntityNotFoundException.class, () -> underTest.resetPassword(request));
            verify(userRepository).findByEmail(eq(request.email()));
        }

        @Test
        @DisplayName("Should throw OTTNotFoundException when no token exists for user")
        void shouldThrowOttNotFoundWhenNoToken() {
            String email = "test@example.com";
            PasswordResetDtoInput request = new PasswordResetDtoInput(email, "newPass");
            UserEntity user = mock(UserEntity.class);
            when(user.getId()).thenReturn(1L);

            given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
            given(repository.existsByUserId(1L)).willReturn(false);

            assertThrowsExactly(OTTNotFoundException.class, () -> underTest.resetPassword(request));
            verify(userRepository).findByEmail(eq(email));
            verify(repository).existsByUserId(eq(1L));
        }
    }
}
