package com.ecommerce.platform.domain.cart.repository;

import com.ecommerce.platform.domain.cart.entity.CartItem;
import com.ecommerce.platform.domain.cart.mapper.CartItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepository {

    private final CartItemMapper cartItemMapper;

    @Override
    public CartItem save(CartItem cartItem) {
        if (cartItem.getId() == null) {
            cartItemMapper.insert(cartItem);
        }
        return cartItem;
    }

    @Override
    public Optional<CartItem> findById(Long id) {
        return Optional.ofNullable(cartItemMapper.findById(id));
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemMapper.findByCartId(cartId);
    }

    @Override
    public Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId) {
        return Optional.ofNullable(cartItemMapper.findByCartIdAndProductId(cartId, productId));
    }

    @Override
    public void updateQuantity(Long id, Integer quantity) {
        cartItemMapper.updateQuantity(id, quantity);
    }

    @Override
    public void updateSelected(Long id, Boolean isSelected) {
        cartItemMapper.updateSelected(id, isSelected);
    }

    @Override
    public void updateAllSelected(Long cartId, Boolean isSelected) {
        cartItemMapper.updateAllSelected(cartId, isSelected);
    }

    @Override
    public void deleteById(Long id) {
        cartItemMapper.deleteById(id);
    }

    @Override
    public void deleteByCartId(Long cartId) {
        cartItemMapper.deleteByCartId(cartId);
    }

    @Override
    public void deleteSelectedByCartId(Long cartId) {
        cartItemMapper.deleteSelectedByCartId(cartId);
    }
}