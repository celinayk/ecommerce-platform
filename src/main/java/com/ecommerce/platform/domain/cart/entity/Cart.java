package com.ecommerce.platform.domain.cart.entity;

import com.ecommerce.platform.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Cart {

    private Long id;
    private User user;
    private List<CartItem> cartItems = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Cart(User user) {
        this.user = user;
    }

    // 장바구니 총 금액 계산
    public Long getTotalPrice() {
        return cartItems.stream()
                .filter(CartItem::getIsSelected)
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    // 선택된 상품 수
    public int getSelectedItemCount() {
        return (int) cartItems.stream()
                .filter(CartItem::getIsSelected)
                .count();
    }

    // 전체 상품 수
    public int getTotalItemCount() {
        return cartItems.size();
    }
}