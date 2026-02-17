package com.ecommerce.platform.domain.stock.service;

import com.ecommerce.platform.domain.stock.dto.StockResponse;
import com.ecommerce.platform.domain.stock.dto.StockUpdateRequest;
import com.ecommerce.platform.domain.stock.entity.Stock;
import com.ecommerce.platform.domain.stock.exception.StockException;
import com.ecommerce.platform.domain.stock.repository.StockRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

  private final StockRepository stockRepository;

  public StockResponse getStock(Long productId) {
    Stock stock = stockRepository.findByProductId(productId)
        .orElseThrow(() -> new StockException(ErrorCode.STOCK_NOT_FOUND));
    return StockResponse.from(stock);
  }

  @Transactional
  public StockResponse updateStock(Long productId, StockUpdateRequest stockUpdateRequest) {
    Stock stock = stockRepository.findByProductIdWithLock(productId)
        .orElseThrow(() -> new StockException(ErrorCode.STOCK_NOT_FOUND));
    stock.updateQuantity(stockUpdateRequest.getQuantity());
    return StockResponse.from(stock);
  }

  // 주문 생성 시 호출 > 재고 차감
  @Transactional
  public void deduct(Long productId, int quantity) {
    Stock stock = stockRepository.findByProductIdWithLock(productId)
        .orElseThrow(() -> new StockException(ErrorCode.STOCK_NOT_FOUND));
    stock.decrease(quantity);
  }

  // 취소/반품 완료 시 호출 > 재고 복구
  @Transactional
  public void restore(Long productId, int quantity) {
    Stock stock = stockRepository.findByProductIdWithLock(productId)
        .orElseThrow(() -> new StockException(ErrorCode.STOCK_NOT_FOUND));
    stock.increase(quantity);
  }


}
