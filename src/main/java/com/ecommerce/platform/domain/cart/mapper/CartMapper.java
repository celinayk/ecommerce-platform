package com.ecommerce.platform.domain.cart.mapper;

import com.ecommerce.platform.domain.cart.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CartMapper {

    // 장바구니 저장
    void insert(Cart cart);

    // ID로 장바구니 조회
    Cart findById(@Param("id") Long id);

    // 사용자 ID로 장바구니 조회
    Cart findByUserId(@Param("userId") Long userId);

    // 장바구니 삭제
    void deleteById(@Param("id") Long id);

    // 사용자 ID로 장바구니 삭제
    void deleteByUserId(@Param("userId") Long userId);
}