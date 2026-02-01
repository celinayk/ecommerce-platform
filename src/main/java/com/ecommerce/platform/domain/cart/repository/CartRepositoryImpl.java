package com.ecommerce.platform.domain.cart.repository;

import com.ecommerce.platform.domain.cart.entity.Cart;
import com.ecommerce.platform.domain.cart.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final CartMapper cartMapper;

    @Override
    public Cart save(Cart cart) {
        if (cart.getId() == null) {
            cartMapper.insert(cart);
        }
        return cart;
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return Optional.ofNullable(cartMapper.findById(id));
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return Optional.ofNullable(cartMapper.findByUserId(userId));
    }

    @Override
    public void deleteById(Long id) {
        cartMapper.deleteById(id);
    }

    @Override
    public void deleteByUserId(Long userId) {
        cartMapper.deleteByUserId(userId);
    }
}