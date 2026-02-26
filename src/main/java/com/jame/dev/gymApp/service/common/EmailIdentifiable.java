package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;

import java.util.Optional;

public interface EmailIdentifiable<T> {
   Optional<T> getByEmail(@EmailValid String email);
}
