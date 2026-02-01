package com.ecommerce.platform.domain.cart.entity;

import com.ecommerce.platform.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CartItem {

    private Long id;
    private Cart cart;
    private Product product;
    private Integer quantity;
    private Boolean isSelected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public CartItem(Cart cart, Product product, Integer quantity, Boolean isSelected) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity != null ? quantity : 1;
        this.isSelected = isSelected != null ? isSelected : true;
    }

    // 연관관계 편의 메서드
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    // 수량 변경
    public void updateQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.quantity = quantity;
    }

    // 수량 증가
    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    // 선택 상태 변경
    public void updateSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    // 소계 계산
    public Long getSubtotal() {
        return product.getPrice() * quantity;
    }
}