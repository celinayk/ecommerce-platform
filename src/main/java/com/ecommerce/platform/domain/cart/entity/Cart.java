package com.ecommerce.platform.domain.cart.entity;

import com.ecommerce.platform.domain.common.BaseEntity;
import com.ecommerce.platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "carts")
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public BigDecimal getTotalPrice() {
        return cartItems.stream()
                .filter(CartItem::getIsSelected)
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getSelectedItemCount() {
        return (int) cartItems.stream()
                .filter(CartItem::getIsSelected)
                .count();
    }

    public int getTotalItemCount() {
        return cartItems.size();
    }
}
