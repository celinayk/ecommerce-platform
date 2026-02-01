package com.ecommerce.platform.domain.cart.mapper;

import com.ecommerce.platform.domain.cart.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {

    // 장바구니 상품 저장
    void insert(CartItem cartItem);

    // ID로 장바구니 상품 조회
    CartItem findById(@Param("id") Long id);

    // 장바구니 ID로 모든 상품 조회
    List<CartItem> findByCartId(@Param("cartId") Long cartId);

    // 장바구니 ID + 상품 ID로 조회 (중복 체크용)
    CartItem findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    // 수량 수정
    void updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    // 선택 상태 수정
    void updateSelected(@Param("id") Long id, @Param("isSelected") Boolean isSelected);

    // 전체 선택/해제
    void updateAllSelected(@Param("cartId") Long cartId, @Param("isSelected") Boolean isSelected);

    // 장바구니 상품 삭제
    void deleteById(@Param("id") Long id);

    // 장바구니 ID로 모든 상품 삭제
    void deleteByCartId(@Param("cartId") Long cartId);

    // 선택된 상품만 삭제
    void deleteSelectedByCartId(@Param("cartId") Long cartId);
}