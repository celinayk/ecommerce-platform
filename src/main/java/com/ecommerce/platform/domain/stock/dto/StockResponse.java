package com.ecommerce.platform.domain.stock.dto;

import com.ecommerce.platform.domain.stock.entity.Stock;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StockResponse {

  private final Long productId;
  private final Integer quantity;
  private final LocalDateTime updatedAt;

  private StockResponse(Stock stock) {
    this.productId = stock.getProduct().getId();
    this.quantity = stock.getQuantity();
    this.updatedAt = stock.getUpdatedAt();
  }

  public static StockResponse from(Stock stock) {
    return new StockResponse(stock);
  }
}

