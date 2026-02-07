package com.ecommerce.platform.domain.cart.repository;

import com.ecommerce.platform.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Query("UPDATE CartItem ci SET ci.isSelected = :isSelected WHERE ci.cart.id = :cartId")
    void updateAllSelected(@Param("cartId") Long cartId, @Param("isSelected") Boolean isSelected);

    void deleteByCartId(Long cartId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.isSelected = true")
    void deleteSelectedByCartId(@Param("cartId") Long cartId);
}
