package com.ecommerce.platform.domain.product.controller;

import com.ecommerce.platform.domain.product.dto.ProductRequest;
import com.ecommerce.platform.domain.product.dto.ProductResponse;
import com.ecommerce.platform.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  // 상품 등록
  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
    ProductResponse response = productService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 상품 목록 조회 (페이징, 정렬)
  @GetMapping
  public ResponseEntity<Page<ProductResponse>> getAllProducts(
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<ProductResponse> responses = productService.getAllProducts(pageable);
    return ResponseEntity.ok(responses);
  }

  // 상품 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
    ProductResponse response = productService.getProductById(id);
    return ResponseEntity.ok(response);
  }

  // 상품 수정
  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable Long id,
      @RequestBody ProductRequest request) {
    ProductResponse response = productService.updateProduct(id, request);
    return ResponseEntity.ok(response);
  }

  // 상품 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}