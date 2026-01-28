package com.jame.dev.gymApp.service.common;

import java.util.Optional;

public interface EmailIdentifiable<T> {
   Optional<T> getByEmail(String email);
}
