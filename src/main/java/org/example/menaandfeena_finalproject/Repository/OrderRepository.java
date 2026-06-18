package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    Orders findOrderById(Integer id);

    List<Orders> findOrdersByUserId(Integer userId);

    List<Orders> findOrdersByMarketPlaceItemUserId(Integer userId);
}
