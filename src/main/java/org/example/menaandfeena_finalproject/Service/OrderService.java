package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.OrderInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.OrderOutDTO;
import org.example.menaandfeena_finalproject.Model.Orders;
import org.example.menaandfeena_finalproject.Repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<OrderOutDTO> getAllOrders() {
        List<OrderOutDTO> orderOutDTOS = new ArrayList<>();

        for (Orders orders : orderRepository.findAll()) {
            orderOutDTOS.add(toOutDTO(orders));
        }

        return orderOutDTOS;
    }

    public void addOrder(OrderInDTO orderInDTO) {
        Orders orders = new Orders();
        orders.setType(orderInDTO.getType());
        orders.setStatus(orderInDTO.getStatus());
        orders.setTotalAmount(orderInDTO.getTotalAmount());
        orders.setStartDate(orderInDTO.getStartDate());
        orders.setEndDate(orderInDTO.getEndDate());

        orderRepository.save(orders);
    }

    public void updateOrder(Integer id, OrderInDTO orderInDTO) {
        Orders oldOrders = orderRepository.findOrderById(id);

        if (oldOrders == null) {
            throw new ApiException("Order not found");
        }

        oldOrders.setType(orderInDTO.getType());
        oldOrders.setStatus(orderInDTO.getStatus());
        oldOrders.setTotalAmount(orderInDTO.getTotalAmount());
        oldOrders.setStartDate(orderInDTO.getStartDate());
        oldOrders.setEndDate(orderInDTO.getEndDate());

        orderRepository.save(oldOrders);
    }

    public void deleteOrder(Integer id) {
        Orders orders = orderRepository.findOrderById(id);

        if (orders == null) {
            throw new ApiException("Order not found");
        }

        orderRepository.delete(orders);
    }

    private OrderOutDTO toOutDTO(Orders orders) {
        return new OrderOutDTO(orders.getId(), orders.getType(), orders.getStatus(), orders.getTotalAmount(), orders.getStartDate(), orders.getEndDate());
    }
}
