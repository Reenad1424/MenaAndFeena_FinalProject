package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    CartItem findCartItemById(Integer id);

    List<CartItem> findCartItemsByCartId(Integer cartId);
}
