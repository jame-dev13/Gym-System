package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.exception.NoOperationException;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.out.SubscriptionServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Membership;
import com.jame.dev.gymApp.shared.enums.Period;
import com.jame.dev.gymApp.shared.enums.Role;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
   @Mock
   private SubscriptionRepository repo;
   @Mock
   private CustomerRepository customerRepo;
   @Mock
   private PricingRepository pricingRepo;
   @InjectMocks
   private SubscriptionServiceImplementation service;

   private SubscriptionDtoInput dtoTest;
   private SubscriptionEntity subscriptionEntityTest;
   private CustomerEntity customerEntityTest;
   private PricingEntity pricingEntityTest;
   private final UserEntity USER_TEST = UserEntity.builder()
           .id(1L)
           .name("userTest")
           .email("test@mail.com")
           .password("testSecret123")
           .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
           .active(true)
           .build();
   private final MemberShipEntity MEMBERSHIP_TEST = new MemberShipEntity(1, Membership.MONTHLY);
   private Long idSubscriptionTest;
   private Long idCustomerTest;
   private Integer idPricingTest;
   @BeforeEach
   void setUp() {
      this.customerEntityTest = new CustomerEntity(1L, USER_TEST, true);
      this.pricingEntityTest = new PricingEntity(1, MEMBERSHIP_TEST, BigDecimal.valueOf(300.00));
      this.subscriptionEntityTest = SubscriptionEntity.builder()
              .id(1L)
              .customer(customerEntityTest)
              .pricing(pricingEntityTest)
              .subscriptionPeriods(List.of(new PeriodEntity(Period.MONTHLY, LocalDate.now())))
              .active(true)
              .finished(false)
              .build();
      dtoTest = SubscriptionDtoInput.builder()
              .customerId(customerEntityTest.getId())
              .pricingId(pricingEntityTest.getId())
              .active(true)
              .finished(false)
              .build();
      this.idSubscriptionTest = subscriptionEntityTest.getId();
      this.idCustomerTest = customerEntityTest.getId();
      this.idPricingTest = pricingEntityTest.getId();
   }

   @Test
   @DisplayName("Get all Actives")
   void getAll() {
      when(repo.findByActiveTrue()).thenReturn(List.of(this.subscriptionEntityTest));
      var subscriptionList = service.getAll();
      verify(repo).findByActiveTrue();

      Assertions.assertAll("Not null, empty, contains and getFirst cases.",
              () -> Assertions.assertNotNull(subscriptionList),
              () -> Assertions.assertFalse(subscriptionList.isEmpty()),
              () -> Assertions.assertTrue(subscriptionList.contains(this.subscriptionEntityTest)),
              () -> Assertions.assertNotNull(subscriptionList.getFirst()));
   }

   @Test
   @DisplayName("Get by id")
   void getById() {
      when(repo.findById(idSubscriptionTest)).thenReturn(Optional.of(this.subscriptionEntityTest));
      var optionalSubs = service.getById(idSubscriptionTest);
      verify(repo).findById(idSubscriptionTest);

      Assertions.assertNotEquals(Optional.empty(), optionalSubs, "Should not be an Optional empty.");
      Assertions.assertDoesNotThrow(optionalSubs::get, "Should doesn't throw Exception");
   }

   @Test
   @DisplayName("Save subscription")
   void save() {
      when(customerRepo.findById(idCustomerTest)).thenReturn(Optional.of(this.customerEntityTest));
      when(pricingRepo.findById(idPricingTest)).thenReturn(Optional.of(this.pricingEntityTest));
      when(repo.save(any(SubscriptionEntity.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      SubscriptionEntity subscriptionAdded = service.save(this.dtoTest);

      ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
      verify(customerRepo).findById(idCustomerTest);
      verify(pricingRepo).findById(idPricingTest);
      verify(repo).save(captor.capture());

      SubscriptionEntity subscriptionSaved = captor.getValue();

      Assertions.assertAll("",
              () -> Assertions.assertNotNull(subscriptionSaved, "Should not be null."),
              () -> Assertions.assertEquals(subscriptionAdded, subscriptionSaved, "Should be the same object.")
      );
   }

   @Test
   void update() {
      Assertions.assertThrows(NoOperationException.class,
              () -> service.update(idSubscriptionTest, dtoTest), "Should throw the Exception.");
   }

   @Test
   void finalizeSubscription() {
      when(repo.findById(idSubscriptionTest)).thenReturn(Optional.of(subscriptionEntityTest));
      when(repo.save(any(SubscriptionEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      service.finalizeSubscription(idSubscriptionTest);

      ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
      verify(repo).findById(idSubscriptionTest);
      verify(repo).save(captor.capture());

      SubscriptionEntity finalizedSubscription = captor.getValue();

      Assertions.assertAll("",
              () -> Assertions.assertNotNull(finalizedSubscription, "Should not be null."),
              () -> Assertions.assertNotEquals(false, finalizedSubscription.getFinished(),
                      "Should be finalized."));
   }

   @Test
   void softDeleteById() {
      service.softDeleteById(idSubscriptionTest);
      verify(repo).softDelete(idSubscriptionTest);
   }
}