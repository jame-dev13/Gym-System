package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jame.dev.gymApp.shared.enums.Period;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;

@Builder
@JsonSerialize
public record PeriodDtoOut(
        @JsonProperty("period") @NonNull Period period,
        @JsonProperty("start") @NonNull LocalDate start,
        @JsonProperty("end") @NonNull LocalDate end
        ) {
}
