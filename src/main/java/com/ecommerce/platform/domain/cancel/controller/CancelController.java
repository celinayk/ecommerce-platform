package com.ecommerce.platform.domain.cancel.controller;

import com.ecommerce.platform.domain.cancel.dto.CancelRequest;
import com.ecommerce.platform.domain.cancel.service.CancelService;
import com.ecommerce.platform.domain.order.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cancels")
@RequiredArgsConstructor
public class CancelController {

  private final CancelService cancelService;

  @PostMapping
  public ResponseEntity<OrderResponse> requestCancel(
      @Valid @RequestBody CancelRequest request
  ) {
    return ResponseEntity.ok(
        cancelService.requestCancel(request.getOrderId(), request.getUserId(), request)
    );
  }

  @PatchMapping("/{cancelId}/status")
  public ResponseEntity<OrderResponse> approveCancel(@PathVariable Long cancelId) {
    return ResponseEntity.ok(cancelService.approveCancel(cancelId));
  }
}