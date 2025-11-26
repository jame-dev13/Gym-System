package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.exception.NoOperationException;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.service.out.PricingServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Membership;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PricingServiceTest {

   @Mock
   private PricingRepository repo;

   @InjectMocks
   private PricingServiceImplementation service;

   private PricingEntity pricingTest;
   private MemberShipEntity memberShipEntityTest;

   @BeforeEach
   void setUp() {
      this.memberShipEntityTest = MemberShipEntity.builder()
              .id(1)
              .membership(Membership.MONTHLY)
              .build();
      this.pricingTest = PricingEntity.builder()
              .id(1)
              .memberShipEntity(memberShipEntityTest)
              .price(BigDecimal.valueOf(300.00d))
              .build();
   }

   @Test
   @DisplayName("Get All")
   void getAll() {
      when(repo.findAll()).thenReturn(List.of(this.pricingTest));
      var pricingList = service.getAll();

      Assertions.assertAll("",
              () -> assertFalse(pricingList.isEmpty(), "List should not be empty."),
              () -> assertTrue(pricingList.contains(this.pricingTest), "List should contains 'pricingTest'."),
              () -> assertEquals(1, pricingList.size(), "The list length should be 1 in this case."),
              () -> assertNotNull(pricingList.getFirst(), "The first element should not be null."),
              () -> assertEquals(pricingList.getFirst(), this.pricingTest, "The elements should be the same.")
      );
      verify(repo).findAll();
   }

   @Test
   @DisplayName("Save Pricing")
   void save() {
      PricingEntity pricing = new PricingEntity(null, pricingTest.getMemberShipEntity(), pricingTest.getPrice());
     when(repo.save(any(PricingEntity.class)))
             .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

     PricingEntity pricingAdded = service.save(pricing);

      ArgumentCaptor<PricingEntity> captor = ArgumentCaptor.forClass(PricingEntity.class);
      verify(repo).save(captor.capture());

      PricingEntity pricingSaved = captor.getValue();
      Assertions.assertNotNull(pricingSaved, "The pricingSaved should not be null.");
      Assertions.assertEquals(pricingSaved, pricingAdded, "The entities should be the same.");
   }

   @Test
   @DisplayName("Find by id.")
   void findById() {
      Integer id = pricingTest.getId();
      when(repo.findById(id)).thenReturn(Optional.of(pricingTest));

      var optionalPricing = service.findById(id);
      var pricing = optionalPricing.orElseThrow();
      verify(repo).findById(id);

      Assertions.assertNotEquals(Optional.empty(), optionalPricing, "The return value should not be Empty.");
      Assertions.assertNotNull(pricing, "Should not be null.");
      Assertions.assertEquals(this.pricingTest, pricing, "The Objects should be the same.");
   }

   @Test
   void deleteById() {
      Integer id = pricingTest.getId();
      Assertions.assertThrows(NoOperationException.class,
              () -> service.deleteById(id),
              "Should throws a NoOperationsExceptions in this case.");
   }
}