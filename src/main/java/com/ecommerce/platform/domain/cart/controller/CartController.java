package com.ecommerce.platform.domain.cart.controller;

import com.ecommerce.platform.domain.cart.dto.CartItemAddRequest;
import com.ecommerce.platform.domain.cart.dto.CartItemResponse;
import com.ecommerce.platform.domain.cart.dto.CartItemUpdateRequest;
import com.ecommerce.platform.domain.cart.dto.CartResponse;
import com.ecommerce.platform.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 장바구니 조회
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    // 장바구니에 상품 추가
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartItemResponse> addCartItem(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemAddRequest request) {
        CartItemResponse response = cartService.addCartItem(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 상품 수량/선택 상태 수정
    @PatchMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemUpdateRequest request) {
        CartItemResponse response = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(response);
    }

    // 전체 선택/해제
    @PatchMapping("/{userId}/items/select-all")
    public ResponseEntity<Void> updateAllSelected(
            @PathVariable Long userId,
            @RequestParam Boolean isSelected) {
        cartService.updateAllSelected(userId, isSelected);
        return ResponseEntity.ok().build();
    }

    // 장바구니 상품 삭제
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        cartService.deleteCartItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    // 선택된 상품 삭제
    @DeleteMapping("/{userId}/items/selected")
    public ResponseEntity<Void> deleteSelectedItems(@PathVariable Long userId) {
        cartService.deleteSelectedItems(userId);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 전체 비우기
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}