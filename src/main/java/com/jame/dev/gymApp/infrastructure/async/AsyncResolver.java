package com.jame.dev.gymApp.infrastructure.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class AsyncResolver {

   public static <T> T getResult(final Supplier<T> supplier, final int timeoutSeconds) {
      try {
         return CompletableFuture
            .supplyAsync(supplier)
            .get(timeoutSeconds, TimeUnit.SECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
         throw new RuntimeException("Timeout reached.", e);
      }
   }
}
