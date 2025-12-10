package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.shared.enums.Membership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SubscriptionMapperTest {
   private final CustomerMapper customerMapper =
           new CustomerMapperImpl(new UserMapperImpl(new RoleMapperImpl()));
   private final PeriodMapper periodMapper = new PeriodMapperImpl();
   private final SubscriptionMapper subscriptionMapper =
           new SubscriptionMapperImpl(customerMapper, periodMapper);
   private CustomerEntity customer;
   private PricingEntity pricing;
   private SubscriptionEntity subs;

   @BeforeEach
   void setup() {
      this.customer = CustomerEntity.builder()
              .id(1L)
              .user(new UserEntity())
              .phoneContact("3926441")
              .active(true)
              .build();
      this.pricing = PricingEntity.builder()
              .id(1)
              .memberShipEntity(new MemberShipEntity(1, Membership.MONTHLY))
              .price(BigDecimal.valueOf(300.00d))
              .build();
      this.subs = SubscriptionEntity.builder()
              .id(1L)
              .customer(customer)
              .pricing(pricing)
              .subscriptionPeriods(List.of(new PeriodEntity()))
              .active(true)
              .finished(false)
              .build();
   }

   @Test
   @DisplayName("To Dto")
   void toDto() {
      final SubscriptionDtoOutput dto = subscriptionMapper.toDto(this.subs);
      final Membership membership = this.subs.getPricing().getMemberShipEntity().getMembership();
      final BigDecimal price = this.subs.getPricing().getPrice();
      assertAll("Not null, properties equals and not finished.",
              () -> assertNotNull(dto, "Should not be null."),
              () -> assertEquals(price, dto.price(), "Should be the same."),
              () -> assertEquals(membership, dto.membership(), "Should be the same."),
              () -> assertFalse(dto.finished(), "Should not be finished."));
   }

   @Test
   @DisplayName("To Entity")
   void toEntity() {
      final SubscriptionDtoInput dtoInput = SubscriptionDtoInput.builder()
              .customerId(1L)
              .pricingId(1)
              .finished(false)
              .build();
      final List<PeriodEntity> periods = new ArrayList<>();

      SubscriptionEntity subs = subscriptionMapper.toEntity(dtoInput, customer, pricing, periods);
      assertAll("Not null, properties equals and not finished.",
              () -> assertNotNull(subs, "Should not be null."),
              () -> assertEquals(customer, subs.getCustomer(), "Should be the same."),
              () -> assertEquals(pricing, subs.getPricing(), "Should be the same."),
              () -> assertEquals(periods, subs.getSubscriptionPeriods(), "Should be the same."),
              () -> assertFalse(subs.isFinished(), "Should be finished."));
   }
}