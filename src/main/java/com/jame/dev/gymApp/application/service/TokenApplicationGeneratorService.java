package com.jame.dev.gymApp.application.service;

import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenApplicationGeneratorService implements TokenGeneratorService {
   private final SecureRandom random = new SecureRandom();
   private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTWVXYZ0123456789";

   @Override
   public String generateToken() {
      final StringBuilder sb = new StringBuilder(6);
      final int capacity = sb.capacity();
      for (int i = 0; i < capacity; i++) {
         final char ch = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
         sb.append(ch);
      }
      return sb.toString();
   }

   @Override
   public String generateTokenOneTimeToken() {
      byte[] bytes = new byte[32];
      random.nextBytes(bytes);
      return Base64.getUrlEncoder()
         .withoutPadding()
         .encodeToString(bytes);
   }
}
