package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.OrderInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.OrderOutDTO;
import org.example.menaandfeena_finalproject.Model.Insurance;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.Orders;
import org.example.menaandfeena_finalproject.Model.Payment;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.InsuranceRepository;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.OrderRepository;
import org.example.menaandfeena_finalproject.Repository.PaymentRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final InsuranceRepository insuranceRepository;

    public List<OrderOutDTO> getAllOrders() {
        List<OrderOutDTO> orderOutDTOS = new ArrayList<>();

        for (Orders orders : orderRepository.findAll()) {
            orderOutDTOS.add(toOutDTO(orders));
        }

        return orderOutDTOS;
    }


    public void deleteOrder(Integer id) {
        Orders orders = orderRepository.findOrderById(id);

        if (orders == null) {
            throw new ApiException("Order not found");
        }

        orderRepository.delete(orders);
    }

    private OrderOutDTO toOutDTO(Orders orders) {
        Integer marketPlaceItemId = orders.getMarketPlaceItem() == null ? null : orders.getMarketPlaceItem().getId();
        Integer userId = orders.getUser() == null ? null : orders.getUser().getId();
        return new OrderOutDTO(orders.getId(), orders.getType(), orders.getStatus(), orders.getTotalAmount(), orders.getStartDate(), orders.getEndDate(), marketPlaceItemId, userId);
    }

    // abdullah

    public OrderOutDTO buyItem(Integer itemId, Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(itemId);

        if (item == null) {
            throw new ApiException("Market place item not found");
        }

        if (!item.getType().equals("SELL")) {
            throw new ApiException("Only sell items can be purchased");
        }

        if (item.getStatus().equals("SOLD")) {
            throw new ApiException("Item is sold");
        }

        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new ApiException("Item quantity is unavailable");
        }

        if (item.getPrice() == null || item.getPrice() <= 0) {
            throw new ApiException("Item price is invalid");
        }

        Orders order = new Orders();
        order.setType("PURCHASE");
        order.setStatus("PAID");
        order.setTotalAmount(item.getPrice());
        order.setStartDate(null);
        order.setEndDate(null);
        order.setMarketPlaceItem(item);
        order.setUser(user);
        Orders savedOrder = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setAmount(item.getPrice());
        payment.setPlatformFee(0);
        payment.setSellerAmount(item.getPrice());
        payment.setStatus("PAID");
        payment.setDescription("Marketplace purchase order #" + savedOrder.getId());
        payment.setOrders(savedOrder);
        paymentRepository.save(payment);

        item.setQuantity(item.getQuantity() - 1);
        if (item.getQuantity() == 0) {
            item.setStatus("SOLD");
        }
        marketPlaceItemRepository.save(item);

        return toOutDTO(savedOrder);
    }

    public OrderOutDTO rentItem(Integer itemId, OrderInDTO orderInDTO) {
        User user = userRepository.findUserById(orderInDTO.getUserId());

        if (user == null) {
            throw new ApiException("User not found");
        }

        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(itemId);

        if (item == null) {
            throw new ApiException("Market place item not found");
        }

        if (!item.getType().equals("RENT")) {
            throw new ApiException("Only rent items can be rented");
        }

        if (!item.getStatus().equals("AVAILABLE")) {
            throw new ApiException("Item is not available for rent");
        }

        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new ApiException("Item quantity is unavailable");
        }

        if (orderInDTO.getStartDate() == null || orderInDTO.getEndDate() == null) {
            throw new ApiException("Start date and end date are required for rent orders");
        }

        if (!orderInDTO.getEndDate().isAfter(orderInDTO.getStartDate())) {
            throw new ApiException("End date must be after start date");
        }

        if (item.getRentPrice() == null || item.getRentPrice() <= 0) {
            throw new ApiException("Item rent price is invalid");
        }

        if (item.getDepositAmount() == null || item.getDepositAmount() < 0) {
            throw new ApiException("Item deposit amount is invalid");
        }

        long numberOfDays = ChronoUnit.DAYS.between(orderInDTO.getStartDate(), orderInDTO.getEndDate());
        Integer amount = Math.toIntExact((item.getRentPrice() * numberOfDays) + item.getDepositAmount());

        Orders order = new Orders();
        order.setType("RENT");
        order.setStatus("ACTIVE");
        order.setTotalAmount(amount);
        order.setStartDate(orderInDTO.getStartDate());
        order.setEndDate(orderInDTO.getEndDate());
        order.setMarketPlaceItem(item);
        order.setUser(user);
        Orders savedOrder = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPlatformFee(0);
        payment.setSellerAmount(amount);
        payment.setStatus("PAID");
        payment.setDescription("Marketplace rent order #" + savedOrder.getId());
        payment.setOrders(savedOrder);
        paymentRepository.save(payment);

        Insurance insurance = new Insurance();
        insurance.setDepositAmount(item.getDepositAmount());
        insurance.setRefundedAmount(0);
        insurance.setStatus("HELD");
        insurance.setHeldAt(LocalDateTime.now());
        insurance.setOrders(savedOrder);
        insuranceRepository.save(insurance);

        item.setQuantity(item.getQuantity() - 1);
        item.setStatus("RENTED");
        marketPlaceItemRepository.save(item);

        return toOutDTO(savedOrder);
    }

    public List<OrderOutDTO> getMyOrders(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<OrderOutDTO> orderOutDTOS = new ArrayList<>();
        for (Orders orders : orderRepository.findOrdersByUserId(userId)) {
            orderOutDTOS.add(toOutDTO(orders));
        }

        return orderOutDTOS;
    }

    public List<OrderOutDTO> getMySales(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<OrderOutDTO> orderOutDTOS = new ArrayList<>();
        for (Orders orders : orderRepository.findOrdersByMarketPlaceItemUserId(userId)) {
            orderOutDTOS.add(toOutDTO(orders));
        }

        return orderOutDTOS;
    }

    public void completeOrder(Integer orderId) {
        Orders order = orderRepository.findOrderById(orderId);

        if (order == null) {
            throw new ApiException("Order not found");
        }

        if (order.getStatus().equals("COMPLETED")) {
            throw new ApiException("Order is already completed");
        }

        if (order.getStatus().equals("CANCELLED")) {
            throw new ApiException("Cancelled order cannot be completed");
        }

        order.setStatus("COMPLETED");

        if (order.getType().equals("RENT") && order.getMarketPlaceItem() != null) {
            MarketPlaceItem item = order.getMarketPlaceItem();
            item.setQuantity(item.getQuantity() + 1);
            item.setStatus("AVAILABLE");
            marketPlaceItemRepository.save(item);
        }

        orderRepository.save(order);
    }

    public void cancelOrder(Integer orderId) {
        Orders order = orderRepository.findOrderById(orderId);

        if (order == null) {
            throw new ApiException("Order not found");
        }

        if (order.getStatus().equals("COMPLETED")) {
            throw new ApiException("Completed order cannot be cancelled");
        }

        if (order.getStatus().equals("CANCELLED")) {
            throw new ApiException("Order is already cancelled");
        }

        order.setStatus("CANCELLED");

        if (order.getMarketPlaceItem() != null) {
            MarketPlaceItem item = order.getMarketPlaceItem();
            item.setQuantity(item.getQuantity() + 1);
            item.setStatus("AVAILABLE");
            marketPlaceItemRepository.save(item);
        }

        orderRepository.save(order);
    }
}
