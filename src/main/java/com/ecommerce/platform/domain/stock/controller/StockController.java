package com.ecommerce.platform.domain.stock.controller;

import com.ecommerce.platform.domain.stock.dto.StockResponse;
import com.ecommerce.platform.domain.stock.dto.StockUpdateRequest;
import com.ecommerce.platform.domain.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/stock")
@RequiredArgsConstructor
public class StockController {

  private final StockService stockService;

  @GetMapping
  public ResponseEntity<StockResponse> getStock(@PathVariable Long productId) {
    return ResponseEntity.ok(stockService.getStock(productId));
  }

  @PatchMapping
  public ResponseEntity<StockResponse> updateStock(
      @PathVariable Long productId,
      @Valid @RequestBody StockUpdateRequest request) {
    return ResponseEntity.ok(stockService.updateStock(productId, request));
  }
}
