package com.ecommerce.platform.domain.cart.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_items_cart", columnList = "cart_id")
})
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected;



    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void updateQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void updateSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
