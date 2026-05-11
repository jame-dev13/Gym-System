package com.jame.dev.gymApp.application.service;

import com.jame.dev.gymApp.application.contract.TokenDBHasherService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class SHA256TokenDBHasherService implements TokenDBHasherService {
   private static final String ALGORITHM = "SHA-256";

   @Override
   public String hashToken(String rawToken) {
      try {
         final MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
         final byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
         return bytesToHex(hashBytes);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException(ALGORITHM + " algorithm not available", e);
      }
   }

   @Override
   public boolean tokenMatches(String rawToken, String hashedToken) {
      final String computedHash = this.hashToken(rawToken);
      return MessageDigest.isEqual(
         computedHash.getBytes(StandardCharsets.UTF_8),
         hashedToken.getBytes(StandardCharsets.UTF_8)
      );
   }

   private static String bytesToHex(byte[] bytes) {
      final StringBuilder hexString = new StringBuilder(bytes.length * 2);
      for (byte b : bytes) {
         final String hex = Integer.toHexString(0xff & b);
         if (hex.length() == 1) {
            hexString.append('0');
         }
         hexString.append(hex);
      }
      return hexString.toString();
   }
}
