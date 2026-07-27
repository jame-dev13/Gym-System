package com.jame.dev.gymApp.infrastructure.io;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Component
public class IOLogReader {

   public void logErrInputStream(final InputStream inputStream) throws IOException {
      try (final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
         reader.lines().forEach(log::error);
      }
   }

}
