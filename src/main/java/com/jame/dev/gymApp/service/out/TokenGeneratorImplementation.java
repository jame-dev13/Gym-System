package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenGeneratorImplementation implements TokenGeneratorService {
   private final SecureRandom random = new SecureRandom();

   @Override
   public String generateToken() {
      byte[] bytes = new byte[6];
      random.nextBytes(bytes);
      return Base64.getUrlEncoder()
         .withoutPadding()
         .encodeToString(bytes);
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
