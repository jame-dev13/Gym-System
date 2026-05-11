package com.jame.dev.gymApp.application.contract;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;

import java.util.Optional;

public interface EmailIdentifiable<T> {
   Optional<T> getByEmail(@EmailValid String email);
}
