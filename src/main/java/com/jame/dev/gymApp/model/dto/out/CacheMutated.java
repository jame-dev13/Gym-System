package com.jame.dev.gymApp.model.dto.out;

import lombok.NonNull;

public record CacheMutated(
        @NonNull String cacheKey
) {
}
