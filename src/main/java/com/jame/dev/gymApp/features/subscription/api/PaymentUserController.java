package com.jame.dev.gymApp.features.subscription.api;


import com.jame.dev.gymApp.features.subscription.api.response.PaymentResponse;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetPaymentPageByCustomerId;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/subscribers/payments")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class PaymentUserController {

   private final GetPaymentPageByCustomerId getPaymentPageByCustomerId;

   @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
   @GetMapping("/{customerId}/customers")
   public ResponseEntity<Page<PaymentResponse>> getPaymentPageByCustomerId(
      @Minimum
      @PathVariable("customerId")
      final long customerId,
      @RequestParam(name = "search", required = false)
      final String search,
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC
      )
      final Pageable pageable) {
      final var pageDto = getPaymentPageByCustomerId.getPageByCustomerId(customerId, search, pageable);
      final Page<PaymentResponse> page = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(page);
   }
}
