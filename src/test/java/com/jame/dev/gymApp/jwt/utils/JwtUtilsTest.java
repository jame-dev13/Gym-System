package com.jame.dev.gymApp.jwt.utils;

import com.jame.dev.gymApp.exception.InvalidSignedJwtKeyException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Optional;

class JwtUtilsTest {

   private JwtUtils jwtUtils;
   private final String VALID_SECRET = "hpBmH5UzH/u738cwN3IDBR7dUPtT151YZL6k7bznVHE=";
   private final String INVALID_SECRET = "INVALID_SECRET";

   @BeforeEach
   void setUp() {
      this.jwtUtils = new JwtUtils();
   }

   @Test
   void signWith() {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      Key signedKey = jwtUtils.signWith();
      Assertions.assertNotNull(signedKey, "Key should not be null.");
      Assertions.assertEquals("HmacSHA256", signedKey.getAlgorithm(), "It should have the same algorithm.");
   }

   @Test
   void invalidSignWith() {
      ReflectionTestUtils.setField(jwtUtils, "secret", INVALID_SECRET);
      Assertions.assertThrows(InvalidSignedJwtKeyException.class, jwtUtils::signWith, "Should throws an Exception.");
   }

   @Test
   void successfulTokenBuild() {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      String token = Assertions.assertDoesNotThrow(() -> jwtUtils.buildToken("Angel", 10_000L),
              "Should return the token object.");
      Assertions.assertNotNull(token, "Token should not be null.");
      Assertions.assertFalse(token.isEmpty(), "Token should not be empty.");
      Assertions.assertFalse(token.isBlank(), "Token should not be blank.");
   }

   @Test
   void failureTokenBuild() {
      ReflectionTestUtils.setField(jwtUtils, "secret", INVALID_SECRET);
      Assertions.assertThrows(InvalidSignedJwtKeyException.class, () -> jwtUtils.buildToken("Angel", 10_000L),
              "Should throwing an Exception.");
   }

   @Test
   void returnsClaim() {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      String token = jwtUtils.buildToken("Angel", 10_000L);
      Optional<String> optionalSubject = Assertions.assertDoesNotThrow(() -> jwtUtils.getClaim(token, Claims::getSubject),
              "Should return the Optional Object.");
      Assertions.assertNotNull(optionalSubject);
      Assertions.assertFalse(optionalSubject.isEmpty());
      Assertions.assertTrue(optionalSubject.isPresent());
   }

   @Test
   void tokenModifiedFails() {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      String token = jwtUtils.buildToken("Angel", 10_000L);
      String modifiedToken = token.concat("123");
      Optional<String> optionalSubject = jwtUtils.getClaim(modifiedToken, Claims::getSubject);
      Assertions.assertTrue(optionalSubject.isEmpty(), "Should be empty.");
      Assertions.assertFalse(optionalSubject.isPresent(), "Should not be present");
      Assertions.assertEquals(Optional.empty(), optionalSubject, "Should be equal to Optional.empty()");
   }

   @Test
   void tokenExpired() throws InterruptedException {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      String token = jwtUtils.buildToken("Angel", 2000L);
      Thread.sleep(3000L);
      Optional<String> optionalSubject = jwtUtils.getClaim(token, Claims::getSubject);
      Assertions.assertTrue(optionalSubject.isEmpty(), "Should be empty.");
      Assertions.assertFalse(optionalSubject.isPresent(), "Should not be present");
      Assertions.assertEquals(Optional.empty(), optionalSubject, "Should be equal to Optional.empty()");
   }
}