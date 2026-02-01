package com.ecommerce.platform.domain.cart.repository;

import com.ecommerce.platform.domain.cart.entity.Cart;

import java.util.Optional;

public interface CartRepository {

    Cart save(Cart cart);

    Optional<Cart> findById(Long id);

    Optional<Cart> findByUserId(Long userId);

    void deleteById(Long id);

    void deleteByUserId(Long userId);
}