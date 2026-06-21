package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findCartById(Integer id);

    Cart findCartByUserId(Integer userId);
}
