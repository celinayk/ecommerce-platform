package com.ecommerce.platform.domain.returns.controller;

import com.ecommerce.platform.domain.order.dto.OrderResponse;
import com.ecommerce.platform.domain.returns.dto.ReturnRequest;
import com.ecommerce.platform.domain.returns.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

  private final ReturnService returnService;

  @PostMapping
  public ResponseEntity<OrderResponse> requestReturn(
      @Valid @RequestBody ReturnRequest request
  ) {
    return ResponseEntity.ok(
        returnService.requestReturn(request.getOrderId(), request.getUserId(), request)
    );
  }

  @PatchMapping("/{returnId}/status")
  public ResponseEntity<OrderResponse> approveReturn(@PathVariable Long returnId) {
    return ResponseEntity.ok(returnService.approveReturn(returnId));
  }

  @PatchMapping("/{returnId}/complete")
  public ResponseEntity<OrderResponse> completeReturn(@PathVariable Long returnId) {
    return ResponseEntity.ok(returnService.completeReturn(returnId));
  }
}