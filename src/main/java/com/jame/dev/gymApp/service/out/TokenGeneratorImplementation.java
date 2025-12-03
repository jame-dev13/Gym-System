package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TokenGeneratorImplementation implements TokenGeneratorService {
   private final SecureRandom random = new SecureRandom();
   private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

   @Override
   public String generateToken() {
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 6; i++) {
         int index = random.nextInt(CHARACTERS.length());
         char character = CHARACTERS.charAt(index);
         sb.append(character);
      }
      return sb.toString();
   }
}
