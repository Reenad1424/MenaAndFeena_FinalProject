package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    Orders findOrderById(Integer id);
}
