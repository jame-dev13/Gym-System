package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.application.contract.TokenDBHasherService;
import com.jame.dev.gymApp.features.auth.application.service.VerificationApplicationService;
import com.jame.dev.gymApp.features.auth.application.support.factory.VerificationFactory;
import com.jame.dev.gymApp.features.auth.application.support.helper.VerificationEvaluatorHelper;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyVerifiedException;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationServiceTest {

    @Mock
    VerificationRepository verificationRepository;
    @Mock
    VerificationFactory verificationFactory;
    @Mock
    VerificationEvaluatorHelper verificationEvaluatorHelper;
    @Mock
    TokenDBHasherService tokenHasherService;

    @InjectMocks
    VerificationApplicationService service;

    private final VerificationEntity verificationEntity = new VerificationEntity();
    private final UserEntity user = new UserEntity();

    @Test
    @DisplayName("Should save verification successfully")
    void save() {
        String tokenHashed = "TokenHashed";
        given(tokenHasherService.hashToken(anyString()))
                .willReturn(tokenHashed);
        given(verificationFactory.createVerification(any(), anyString()))
                .willReturn(verificationEntity);
        given(verificationRepository.saveAndFlush(any()))
                .willReturn(verificationEntity);

        final var result = service.save(user, "rawToken");

        assertNotNull(result);

        then(tokenHasherService).should().hashToken(anyString());
        then(verificationFactory).should(times(1)).createVerification(any(), anyString());
        then(verificationRepository).should(times(1)).saveAndFlush(any());
        verifyNoMoreInteractions(verificationFactory, tokenHasherService, verificationRepository);
        verifyNoInteractions(verificationEvaluatorHelper);
    }

    @Test
    @DisplayName("Should verify by email successfully")
    void verifyByEmail_Success() {
        String email = "mail@verified.com";
        VerificationEntity verification = mock(VerificationEntity.class);

        given(verification.isVerified()).willReturn(false);
        given(verification.getToken()).willReturn("tokenHashed");
        given(verification.getExpiration()).willReturn(Instant.now().plus(10, ChronoUnit.MINUTES));
        given(verificationRepository.findByUser_Email(email))
                .willReturn(Optional.of(verification));
        given(verificationRepository.saveAndFlush(any(VerificationEntity.class)))
                .willReturn(verification);

        assertDoesNotThrow(() -> service.verify(email, "rawToken"));

        then(verificationRepository).should().findByUser_Email(email);
        then(verificationEvaluatorHelper).should()
                .evaluateVerificationToken(anyString(), anyString(), any());
        then(verification).should().setVerified(true);
        then(verificationRepository).should().saveAndFlush(verification);
        verifyNoMoreInteractions(verificationRepository, verificationEvaluatorHelper);
        verifyNoInteractions(verificationFactory, tokenHasherService);
    }

    @Test
    @DisplayName("Should throw AlreadyVerifiedException when account already verified by email")
    void verifyByEmail_AlreadyVerified() {
        String email = "mail@verified.com";
        VerificationEntity verification = mock(VerificationEntity.class);

        given(verification.isVerified()).willReturn(true);
        given(verificationRepository.findByUser_Email(email))
                .willReturn(Optional.of(verification));

        assertThrowsExactly(
                AlreadyVerifiedException.class,
                () -> service.verify(email, "rawToken")
        );

        then(verificationRepository).should().findByUser_Email(email);
        verifyNoMoreInteractions(verificationRepository);
        verifyNoInteractions(verificationFactory, tokenHasherService, verificationEvaluatorHelper);
    }

    @Test
    @DisplayName("Should throw VerificationNotFoundException when email not found")
    void verifyByEmail_NotFound() {
        given(verificationRepository.findByUser_Email(anyString()))
                .willReturn(Optional.empty());

        assertThrowsExactly(
                VerificationNotFoundException.class,
                () -> service.verify("email@mail.com", "rawToken")
        );

        then(verificationRepository).should().findByUser_Email(anyString());
        verifyNoMoreInteractions(verificationRepository);
        verifyNoInteractions(verificationFactory, tokenHasherService, verificationEvaluatorHelper);
    }

    @Test
    @DisplayName("Should throw VerificationAttemptFailedException when token invalid by email")
    void verifyByEmail_AttemptFailed() {
        VerificationEntity verification = mock(VerificationEntity.class);

        given(verification.isVerified()).willReturn(false);
        given(verification.getToken()).willReturn("tokenHashed");
        given(verification.getExpiration()).willReturn(Instant.now().plus(10, ChronoUnit.MINUTES));
        given(verificationRepository.findByUser_Email(anyString()))
                .willReturn(Optional.of(verification));
        doThrow(new VerificationAttemptFailedException("Token invalid."))
                .when(verificationEvaluatorHelper)
                .evaluateVerificationToken(anyString(), anyString(), any());

        assertThrowsExactly(
                VerificationAttemptFailedException.class,
                () -> service.verify("email@mail.com", "rawToken")
        );

        then(verificationRepository).should().findByUser_Email(anyString());
        then(verificationEvaluatorHelper).should()
                .evaluateVerificationToken(anyString(), anyString(), any());
        verifyNoMoreInteractions(verificationRepository, verificationEvaluatorHelper);
        verifyNoInteractions(verificationFactory, tokenHasherService);
    }

    @Test
    @DisplayName("Should verify entity successfully")
    void verifyEntity_Success() {
        VerificationEntity verification = mock(VerificationEntity.class);

        given(verification.isVerified()).willReturn(false);
        given(verification.getToken()).willReturn("tokenHashed");
        given(verification.getExpiration()).willReturn(Instant.now().plus(10, ChronoUnit.MINUTES));
        given(verificationRepository.saveAndFlush(any(VerificationEntity.class)))
                .willReturn(verification);

        assertDoesNotThrow(() -> service.verify(verification, "rawToken"));

        then(verificationEvaluatorHelper).should()
                .evaluateVerificationToken(anyString(), anyString(), any());
        then(verification).should().setVerified(true);
        then(verificationRepository).should().saveAndFlush(verification);
        verifyNoMoreInteractions(verificationRepository, verificationEvaluatorHelper);
        verifyNoInteractions(verificationFactory, tokenHasherService);
    }

    @Test
    @DisplayName("Should throw AlreadyVerifiedException when entity already verified")
    void verifyEntity_AlreadyVerified() {
        VerificationEntity verification = mock(VerificationEntity.class);

        given(verification.isVerified()).willReturn(true);

        assertThrowsExactly(
                AlreadyVerifiedException.class,
                () -> service.verify(verification, "rawToken")
        );

        verifyNoMoreInteractions(verificationRepository, verificationEvaluatorHelper);
        verifyNoInteractions(verificationFactory, tokenHasherService);
    }

    @Test
    @DisplayName("Should throw VerificationAttemptFailedException when entity token invalid")
    void verifyEntity_AttemptFailed() {
        VerificationEntity verification = mock(VerificationEntity.class);

        given(verification.isVerified()).willReturn(false);
        given(verification.getToken()).willReturn("tokenHashed");
        given(verification.getExpiration()).willReturn(Instant.now().plus(10, ChronoUnit.MINUTES));
        doThrow(new VerificationAttemptFailedException("Token invalid."))
                .when(verificationEvaluatorHelper)
                .evaluateVerificationToken(anyString(), anyString(), any());

        assertThrowsExactly(
                VerificationAttemptFailedException.class,
                () -> service.verify(verification, "rawToken")
        );

        then(verificationEvaluatorHelper).should()
                .evaluateVerificationToken(anyString(), anyString(), any());
        verifyNoMoreInteractions(verificationRepository, verificationEvaluatorHelper);
        verifyNoInteractions(verificationFactory, tokenHasherService);
    }

    @Test
    @DisplayName("Should get verification by email")
    void getByUserEmail() {
        given(verificationRepository.findByUser_Email(anyString()))
                .willReturn(Optional.of(verificationEntity));

        var result = service.getByUserEmail("email@mail.com");

        assertNotNull(result);
        then(verificationRepository).should().findByUser_Email(anyString());
        verifyNoMoreInteractions(verificationRepository);
        verifyNoInteractions(verificationFactory, verificationEvaluatorHelper, tokenHasherService);
    }

    @Test
    @DisplayName("Should throw VerificationNotFoundException when no verification for email")
    void getByUserEmail_NotFound() {
        given(verificationRepository.findByUser_Email(anyString()))
                .willReturn(Optional.empty());

        assertThrowsExactly(
                VerificationNotFoundException.class,
                () -> service.getByUserEmail("email@mail.com")
        );

        then(verificationRepository).should().findByUser_Email(anyString());
        verifyNoMoreInteractions(verificationRepository);
        verifyNoInteractions(verificationFactory, verificationEvaluatorHelper, tokenHasherService);
    }

    @Test
    @DisplayName("Should update verification entity")
    void update() {
        String rawToken = "newRawToken";
        String hashedToken = "newHashedToken";
        given(tokenHasherService.hashToken(rawToken)).willReturn(hashedToken);
        given(verificationRepository.saveAndFlush(verificationEntity)).willReturn(verificationEntity);

        service.update(verificationEntity, rawToken);

        then(tokenHasherService).should().hashToken(rawToken);
        assertEquals(hashedToken, verificationEntity.getToken());
        assertNotNull(verificationEntity.getExpiration());
        then(verificationRepository).should().saveAndFlush(verificationEntity);
        verifyNoMoreInteractions(tokenHasherService, verificationRepository);
        verifyNoInteractions(verificationFactory, verificationEvaluatorHelper);
    }

    @Test
    @DisplayName("Should return true when email is verified")
    void isVerified() {
        given(verificationRepository.existsByUser_EmailAndVerifiedTrue(anyString()))
                .willReturn(true);

        assertTrue(service.isVerified("verified@verified.com"));

        then(verificationRepository).should(times(1))
                .existsByUser_EmailAndVerifiedTrue(anyString());
        verifyNoMoreInteractions(verificationRepository);
        verifyNoInteractions(verificationFactory, verificationEvaluatorHelper, tokenHasherService);
    }

    @Test
    @DisplayName("Should return true when verification is deactivated")
    void checkVerifiedDeactivated() {
        given(verificationRepository.existsDeactivatedByUser_Email(anyString()))
                .willReturn(true);

        assertTrue(service.checkVerifiedDeactivated("email@mail.com"));

        then(verificationRepository).should()
                .existsDeactivatedByUser_Email(anyString());
        verifyNoMoreInteractions(verificationRepository);
        verifyNoInteractions(verificationFactory, verificationEvaluatorHelper, tokenHasherService);
    }

    @Test
    @DisplayName("Should return true when verification exists for email")
    void verificationExistsFor() {
        given(verificationRepository.existsByUser_Email(anyString()))
                .willReturn(true);

        assertTrue(service.verificationExistsFor("email@mail.com"));

        then(verificationRepository).should()
                .existsByUser_Email(anyString());
        verifyNoMoreInteractions(verificationRepository);
        verifyNoInteractions(verificationFactory, verificationEvaluatorHelper, tokenHasherService);
    }
}
