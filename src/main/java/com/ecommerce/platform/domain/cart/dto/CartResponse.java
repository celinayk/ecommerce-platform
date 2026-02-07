package com.ecommerce.platform.domain.cart.dto;

import com.ecommerce.platform.domain.cart.entity.Cart;
import com.ecommerce.platform.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CartResponse {

    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private int totalItemCount;
    private int selectedItemCount;
    private BigDecimal totalPrice;

    public static CartResponse from(Cart cart, List<CartItem> cartItems) {
        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());

        BigDecimal totalPrice = cartItems.stream()
                .filter(CartItem::getIsSelected)
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int selectedCount = (int) cartItems.stream()
                .filter(CartItem::getIsSelected)
                .count();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemResponses)
                .totalItemCount(cartItems.size())
                .selectedItemCount(selectedCount)
                .totalPrice(totalPrice)
                .build();
    }
}