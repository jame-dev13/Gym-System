package com.jame.dev.gymApp.model.dto.out;

public record PageMetaData(
        int number, int size, long totalElements, int totalPages,
        String sortProperty, String sortDirection
) {
}
