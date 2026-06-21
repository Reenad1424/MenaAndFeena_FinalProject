package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    OrderItem findOrderItemById(Integer id);

    List<OrderItem> findOrderItemsByOrdersId(Integer orderId);
}
