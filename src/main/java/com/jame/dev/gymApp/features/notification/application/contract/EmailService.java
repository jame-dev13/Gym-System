package com.jame.dev.gymApp.features.notification.application.contract;

import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
   CompletableFuture<Boolean> sendSimpleEmail(@NonNull final EmailDetails emailDetails);
}
