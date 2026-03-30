package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.AlreadyVerifiedException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.factories.VerificationFactory;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class VerificationServiceImplementation implements VerificationService {
   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final VerificationRepository verificationRepository;
   private final VerificationFactory verificationFactory;

   @Override
   @Transactional
   public VerificationEntity save(final long userId, String token) {
      final UserEntity user = userRepository.findById(userId)
              .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      final VerificationEntity verification = verificationFactory.createVerification(user, passwordEncoder.encode(token));
      return verificationRepository.saveAndFlush(verification);
   }

   @Override
   @Transactional
   public void verify(final String email, final String token) {
      final VerificationEntity verification = verificationRepository.findByUser_Email(email)
              .orElseThrow(() -> new VerificationNotFoundException("Verification not found: " + email));

      if(verification.isVerified())
         throw new AlreadyVerifiedException("Account has already been verified.");

      final String extractToken = verification.getToken();

      final boolean isSameToken = passwordEncoder.matches(token, extractToken);
      final boolean isValid = Instant.now().isBefore(verification.getExpiration());

      if (!isSameToken || !isValid) {
         String invalidTokenMsg = "Token invalid.";
         String attemptInvalid = "Invalid attempt.";
         throw new VerificationAttemptFailedException(!isSameToken ? invalidTokenMsg : attemptInvalid);
      }

      verification.setVerified(true);
      verificationRepository.saveAndFlush(verification);
   }

   @Override
   @Transactional
   public void update(String email, String rawToken) {
      final VerificationEntity verificationEntity = verificationRepository.findDeactivatedByUser_Email(email)
              .orElseThrow(() -> new VerificationNotFoundException("Verification not found."));
      verificationEntity.setToken(passwordEncoder.encode(rawToken));
      verificationEntity.setExpiration(Instant.now().plus(10, ChronoUnit.MINUTES));
      verificationRepository.saveAndFlush(verificationEntity);
   }

   @Override
   public boolean isVerified(@EmailValid String email) {
      return verificationRepository.existsByUser_EmailAndVerifiedTrue(email);
   }

   @Override
   public boolean checkVerifiedDeactivated(String email) {
      return verificationRepository.existsDeactivatedByUser_Email(email);
   }

//   @Override
//   @Transactional
//   public void delete(@NonNull String token) {
//      final VerificationEntity verification = verificationRepository.findById(token)
//              .orElseThrow(() -> new VerificationNotFoundException("Verification entity not found: " + token));
//      if (verification.isVerified()) return;
//      verificationRepository.deleteById(token);

//   }
}
