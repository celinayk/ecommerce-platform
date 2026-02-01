package com.ecommerce.platform.domain.cart.repository;

import com.ecommerce.platform.domain.cart.entity.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {

    CartItem save(CartItem cartItem);

    Optional<CartItem> findById(Long id);

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    void updateQuantity(Long id, Integer quantity);

    void updateSelected(Long id, Boolean isSelected);

    void updateAllSelected(Long cartId, Boolean isSelected);

    void deleteById(Long id);

    void deleteByCartId(Long cartId);

    void deleteSelectedByCartId(Long cartId);
}