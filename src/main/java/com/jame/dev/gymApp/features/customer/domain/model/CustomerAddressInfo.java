package com.jame.dev.gymApp.features.customer.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAddressInfo {

   @Column(name = "city", length = 30)
   @JsonProperty("city")
   private String city;

   @Column(name = "locality", length = 30)
   @JsonProperty("locality")
   private String locality;

   @Column(name = "street", length = 50)
   @JsonProperty("street")
   private String street;

   @Column(name = "colony", length = 30)
   @JsonProperty("colony")
   private String colony;

   @Column(name = "home_number")
   @JsonProperty("homeNumber")
   private String homeNumber;

   @Column(name = "cp")
   @JsonProperty("cp")
   private String cp;
}
