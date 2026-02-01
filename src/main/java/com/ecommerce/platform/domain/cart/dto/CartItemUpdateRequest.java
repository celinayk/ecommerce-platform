package com.ecommerce.platform.domain.cart.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemUpdateRequest {

    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity;

    private Boolean isSelected;

    @Builder
    public CartItemUpdateRequest(Integer quantity, Boolean isSelected) {
        this.quantity = quantity;
        this.isSelected = isSelected;
    }
}
