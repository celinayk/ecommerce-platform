package com.ecommerce.platform.domain.cart.dto;

import com.ecommerce.platform.domain.cart.entity.CartItem;
import com.ecommerce.platform.domain.product.entity.ProductStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Long productPrice;
    private Long productStock;
    private ProductStatus productStatus;
    private Integer quantity;
    private Boolean isSelected;
    private Long subtotal;

    public static CartItemResponse from(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productPrice(cartItem.getProduct().getPrice())
                .productStock(cartItem.getProduct().getStock())
                .productStatus(cartItem.getProduct().getStatus())
                .quantity(cartItem.getQuantity())
                .isSelected(cartItem.getIsSelected())
                .subtotal(cartItem.getSubtotal())
                .build();
    }
}