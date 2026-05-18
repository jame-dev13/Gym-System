package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.MemberShipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.PricingRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionRepository;
import com.jame.dev.gymApp.features.subscription.application.service.SubscriptionApplicationService;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.support.validator.SubscriptionValidator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class SubscriptionServiceTest {
   @Mock
   SubscriptionRepository repo;

   @Mock
   CustomerRepository customerRepo;

   @Mock
   PricingRepository pricingRepo;

   @Mock
   SubscriptionFactory subscriptionFactory;

   @Mock
   SubscriptionUpdater subscriptionUpdater;

   @Mock
   SubscriptionValidator validator;

   @InjectMocks
   SubscriptionApplicationService service;

   private final MemberShipEntity MEMBERSHIP_TEST = new MemberShipEntity(1, Membership.MONTHLY);
   private final PricingEntity pricingMock = new PricingEntity(1, MEMBERSHIP_TEST, BigDecimal.valueOf(300.00));
   private final UserEntity userMock = new UserEntity();
   private final CustomerEntity customerMock = new CustomerEntity();
   private final SubscriptionRequest dtoMock = new SubscriptionRequest("customer@mail.com", Membership.MONTHLY);
   private final SubscriptionEntity mockSubscription = new SubscriptionEntity();

   private final List<SubscriptionEntity> testSubsList = IntStream.range(0, 10)
      .mapToObj(i -> {
         CustomerEntity customer = new CustomerEntity(
            new UserEntity(), "32472525" + i);
         PricingEntity pricing = new PricingEntity(
            (i + 1),
            new MemberShipEntity(
               (i + 1),
               Membership.MONTHLY),
            BigDecimal.valueOf(300.00d)
         );
         return SubscriptionEntity.builder()
            .customer(customer)
            .pricing(pricing)
            .subscriptionPeriods(List.of(new PeriodEntity()))
            .finished(false)
            .build();
      })
      .toList();

   @BeforeEach
   void setUp() {
      customerMock.setUser(userMock);
      mockSubscription.setCustomer(customerMock);
   }

   @Test
   @DisplayName("Should Get Page of subscriptions")
   void getPageByActive() {
      final String search = "id=10";
      final Pageable pageable = PageRequest.of(0, 5);
      final List<SubscriptionEntity> subList = testSubsList.subList(0, 5);
      PageDto<SubscriptionResponse> pageDto = mock();
      when(repo.findAll(pageable)).thenReturn(new PageImpl<>(subList));
      when(subscriptionFactory.createPageFrom(any())).thenReturn(pageDto);

      final var page = service.getPage(pageable, search);
      final var pageContent = page.content();

      assertNotNull(page, "Page shouldn't be null");
      assertNotNull(pageContent, "Page content shouldn't be null");

      verify(repo, atLeastOnce()).findAll(pageable);
      verify(subscriptionFactory, atLeastOnce()).createPageFrom(any());
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should Get subscription by id")
   void getById() {
      SubscriptionResponse output = mock();
      when(repo.findById(anyLong())).thenReturn(Optional.of(new SubscriptionEntity()));
      when(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class)))
         .thenReturn(output);

      final var result = assertDoesNotThrow(() -> service.getById(anyLong()));

      assertNotNull(result, "Result shouldn't be null.");

      verify(repo, atMostOnce()).findById(anyLong());
      verify(subscriptionFactory, atMostOnce()).createFromEntity(any());
      verifyNoMoreInteractions(repo, subscriptionFactory);
   }

   @Test
   @DisplayName("Save subscription")
   void save() {
      SubscriptionResponse output = mock(SubscriptionResponse.class);
      CustomerEntity customerMock = mock(CustomerEntity.class);
      PricingEntity pricingMock = mock(PricingEntity.class);
      SubscriptionEntity subscriptionMock = mock(SubscriptionEntity.class);

      when(customerRepo.findByUser_Email(anyString()))
         .thenReturn(Optional.of(customerMock));
      when(repo.existsByCustomer(any(CustomerEntity.class)))
         .thenReturn(false);
      when(pricingRepo.findByMemberShipEntity_Membership(any(Membership.class)))
         .thenReturn(Optional.of(pricingMock));
      when(subscriptionFactory.createFromInput(any(SubscriptionFactoryDtoInput.class)))
         .thenReturn(subscriptionMock);
      when(repo.saveAndFlush(any(SubscriptionEntity.class)))
         .thenReturn(subscriptionMock);
      when(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class)))
         .thenReturn(output);

      final var result = assertDoesNotThrow(() -> service.save(this.dtoMock));

      assertNotNull(result);

      verify(customerRepo, atMostOnce()).findByUser_Email(anyString());
      verify(repo, atMostOnce()).existsByCustomer(any(CustomerEntity.class));
      verify(pricingRepo, atMostOnce()).findByMemberShipEntity_Membership(any(Membership.class));
      verify(subscriptionFactory, atMostOnce())
         .createFromInput(any(SubscriptionFactoryDtoInput.class));
      verify(repo, atLeastOnce()).saveAndFlush(any(SubscriptionEntity.class));
      verify(subscriptionFactory, atMostOnce()).createFromEntity(any(SubscriptionEntity.class));
      verifyNoMoreInteractions(repo, customerRepo, pricingRepo, subscriptionFactory);
   }

   @Test
   @DisplayName("Customer not found")
   void shouldThrowsCustomerNotFound() {
      doThrow(CustomerNotFoundException.class).when(customerRepo).findByUser_Email(anyString());
      assertThrows(CustomerNotFoundException.class, () -> service.save(dtoMock));

      verify(customerRepo, atMostOnce()).findByUser_Email(anyString());
      verifyNoInteractions(repo);
      verifyNoMoreInteractions(customerRepo);
   }

   @Test
   @DisplayName("Exists by customer")
   void shouldThrowsAlreadyExistsException() {
      when(customerRepo.findByUser_Email(anyString())).thenReturn(Optional.of(customerMock));
      doThrow(AlreadyExistsException.class).when(repo).existsByCustomer(customerMock);

      assertThrowsExactly(AlreadyExistsException.class, () -> service.save(dtoMock));

      verify(customerRepo, atMostOnce()).findByUser_Email(anyString());
      verify(repo, atMostOnce()).existsByCustomer(any(CustomerEntity.class));
      verifyNoMoreInteractions(repo, customerRepo);
   }

   @Test
   @DisplayName("Should update the SubscriptionEntity")
   void update() {
      SubscriptionResponse output = mock(SubscriptionResponse.class);
      SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
      subscriptionEntity.setUpdatedAt(Instant.now());

      when(repo.findById(anyLong()))
         .thenReturn(Optional.of(subscriptionEntity));
      when(pricingRepo.findByMemberShipEntity_Membership(any(Membership.class)))
         .thenReturn(Optional.of(new PricingEntity()));
      doNothing().when(subscriptionUpdater).apply(
         any(SubscriptionEntity.class),
         any(PricingEntity.class)
      );
      when(repo.saveAndFlush(any(SubscriptionEntity.class))).
         thenReturn(subscriptionEntity);
      when(subscriptionFactory.createFromEntity(any()))
         .thenReturn(output);

      assertDoesNotThrow(() -> service.update(1L, dtoMock), "Should doesn't throw any exception");

      verify(repo, atMostOnce()).findById(anyLong());
      verify(pricingRepo, atMostOnce()).findByMemberShipEntity_Membership(any(Membership.class));
      verify(subscriptionUpdater, atMostOnce()).apply(any(SubscriptionEntity.class), any(PricingEntity.class));
      verify(repo, atMostOnce()).saveAndFlush(subscriptionEntity);
      verify(subscriptionFactory, atMostOnce()).createFromEntity(any());
      verifyNoMoreInteractions(repo, pricingRepo, subscriptionUpdater, subscriptionFactory);
   }

   @Test
   @DisplayName("Should finalize the subscription [patch]")
   void finalizeSubscription() {
      SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
      subscriptionEntity.setFinished(true);
      subscriptionEntity.setUpdatedAt(Instant.now());
      SubscriptionResponse output = mock(SubscriptionResponse.class);

      when(repo.findById(anyLong()))
         .thenReturn(Optional.of(new SubscriptionEntity()));
      when(repo.saveAndFlush(any(SubscriptionEntity.class)))
         .thenReturn(subscriptionEntity);
      when(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class)))
         .thenReturn(output);

      final var result = assertDoesNotThrow(() -> service.patch(1L));
      assertNotNull(result);
      verify(repo, atMostOnce()).findById(anyLong());
      verify(repo, atMostOnce()).saveAndFlush(subscriptionEntity);
      verify(subscriptionFactory, atMostOnce()).createFromEntity(any(SubscriptionEntity.class));
      verifyNoMoreInteractions(repo, subscriptionFactory);
   }

   @Test
   @DisplayName("Should renew the subscription")
   void shouldRenewSubscription() {
      String email = "sample@mail.com";
      SubscriptionRequest dtoMock = new SubscriptionRequest(email, Membership.MONTHLY);

      SubscriptionEntity subscription = mock(SubscriptionEntity.class);
      SubscriptionResponse output = mock(SubscriptionResponse.class);

      when(validator.validateOnRenew(anyLong(), any(SubscriptionRequest.class)))
         .thenReturn(subscription);
      when(pricingRepo.findByMemberShipEntity_Membership(any(Membership.class)))
         .thenReturn(Optional.of(pricingMock));
      doNothing().when(subscriptionUpdater)
         .applyRenew(any(SubscriptionEntity.class), any(PricingEntity.class));
      when(repo.saveAndFlush(any(SubscriptionEntity.class))).thenReturn(subscription);
      when(subscriptionFactory.createFromEntity(any())).thenReturn(output);

      final var result = assertDoesNotThrow(() -> service.put(1L, dtoMock));
      assertNotNull(result);

      verify(validator).validateOnRenew(anyLong(), any(SubscriptionRequest.class));
      verify(pricingRepo).findByMemberShipEntity_Membership(any(Membership.class));
      verify(subscriptionUpdater).applyRenew(any(SubscriptionEntity.class), any(PricingEntity.class));
      verify(repo).saveAndFlush(subscription);
      verify(subscriptionFactory, atMostOnce()).createFromEntity(any());
      verifyNoMoreInteractions(repo, pricingRepo, subscriptionFactory, subscriptionUpdater, validator);
   }

   @Test
   @DisplayName("Should delete subscription by Id.")
   void softDeleteById() {
      service.softDelete(1L);
      verify(repo, times(1)).deleteById(anyLong());
   }
}