package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AnnualResumeResponse(
   @JsonProperty("totalPaymentsMade") long totalPaymentsMade,
   @JsonProperty("totalExpend") BigDecimal totalExpend,
   @JsonProperty("average") BigDecimal average,
   @JsonProperty("electronicPaymentsDone") long electronicPaymentsDone,
   @JsonProperty("physicPaymentsDone") long physicPaymentsDone
) {
}
