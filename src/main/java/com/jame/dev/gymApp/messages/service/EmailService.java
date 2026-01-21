package com.jame.dev.gymApp.messages.service;

import com.jame.dev.gymApp.model.messages.EmailDetails;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
   CompletableFuture<Boolean> sendSimpleEmail(@NonNull final EmailDetails emailDetails);
}
