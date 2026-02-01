package com.ecommerce.platform.domain.cart.service;

import com.ecommerce.platform.domain.cart.dto.CartItemAddRequest;
import com.ecommerce.platform.domain.cart.dto.CartItemResponse;
import com.ecommerce.platform.domain.cart.dto.CartItemUpdateRequest;
import com.ecommerce.platform.domain.cart.dto.CartResponse;
import com.ecommerce.platform.domain.cart.entity.Cart;
import com.ecommerce.platform.domain.cart.entity.CartItem;
import com.ecommerce.platform.domain.cart.exception.CartException;
import com.ecommerce.platform.domain.cart.repository.CartItemRepository;
import com.ecommerce.platform.domain.cart.repository.CartRepository;
import com.ecommerce.platform.domain.product.entity.Product;
import com.ecommerce.platform.domain.product.exception.ProductException;
import com.ecommerce.platform.domain.product.repository.ProductRepository;
import com.ecommerce.platform.domain.user.entity.User;
import com.ecommerce.platform.domain.user.exception.UserException;
import com.ecommerce.platform.domain.user.repository.UserRepository;
import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 장바구니 조회
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        return CartResponse.from(cart, cartItems);
    }

    // 장바구니에 상품 추가
    @Transactional
    public CartItemResponse addCartItem(Long userId, CartItemAddRequest request) {
        Cart cart = getOrCreateCart(userId);

        // 상품 조회
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));

        // 이미 장바구니에 있는 상품인지 확인
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            // 이미 있으면 수량 증가
            existingItem.increaseQuantity(request.getQuantity());
            cartItemRepository.updateQuantity(existingItem.getId(), existingItem.getQuantity());
            return CartItemResponse.from(existingItem);
        }

        // 새 상품 추가
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .isSelected(true)
                .build();

        cartItemRepository.save(cartItem);
        return CartItemResponse.from(cartItem);
    }

    // 장바구니 상품 수량/선택 상태 수정
    @Transactional
    public CartItemResponse updateCartItem(Long userId, Long cartItemId, CartItemUpdateRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (request.getQuantity() != null) {
            cartItem.updateQuantity(request.getQuantity());
            cartItemRepository.updateQuantity(cartItemId, request.getQuantity());
        }

        if (request.getIsSelected() != null) {
            cartItem.updateSelected(request.getIsSelected());
            cartItemRepository.updateSelected(cartItemId, request.getIsSelected());
        }

        return CartItemResponse.from(cartItem);
    }

    // 전체 선택/해제
    @Transactional
    public void updateAllSelected(Long userId, Boolean isSelected) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartException(ErrorCode.CART_NOT_FOUND));

        cartItemRepository.updateAllSelected(cart.getId(), isSelected);
    }

    // 장바구니 상품 삭제
    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.deleteById(cartItemId);
    }

    // 선택된 상품 삭제
    @Transactional
    public void deleteSelectedItems(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartException(ErrorCode.CART_NOT_FOUND));

        cartItemRepository.deleteSelectedByCartId(cart.getId());
    }

    // 장바구니 전체 비우기
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartException(ErrorCode.CART_NOT_FOUND));

        cartItemRepository.deleteByCartId(cart.getId());
    }

    // 사용자 장바구니 조회 또는 생성
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }
}