package com.jame.dev.gymApp.application.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.lock.preventive")
public record LockProperties(int lifetimeMinutes, String key) {
}
