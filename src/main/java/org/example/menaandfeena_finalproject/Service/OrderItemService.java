package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.OrderItemInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.InsuranceOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.OrderItemOutDTO;
import org.example.menaandfeena_finalproject.Model.Insurance;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.OrderItem;
import org.example.menaandfeena_finalproject.Model.Orders;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.InsuranceRepository;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.OrderItemRepository;
import org.example.menaandfeena_finalproject.Repository.OrderRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final InsuranceRepository insuranceRepository;
    private final UserRepository userRepository;

    public List<OrderItemOutDTO> getAllOrderItems() {
        List<OrderItemOutDTO> orderItemOutDTOS = new ArrayList<>();

        for (OrderItem orderItem : orderItemRepository.findAll()) {
            String itemName = orderItem.getMarketPlaceItem() == null ? null : orderItem.getMarketPlaceItem().getTitle();
            Insurance insurance = orderItem.getInsurance();
            InsuranceOutDTO insuranceOutDTO = null;
            if (insurance != null) {
                Integer orderItemId = insurance.getOrderItem() == null ? null : insurance.getOrderItem().getId();
                insuranceOutDTO = new InsuranceOutDTO(insurance.getId(), insurance.getDepositAmount(), insurance.getRefundedAmount(), insurance.getStatus(), insurance.getHeldAt(), insurance.getRefundedAt(), insurance.getRefundTransactionId(), orderItemId);
            }

            if (orderItem.getType().equals("RENT")) {
                orderItemOutDTOS.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), orderItem.getRentalDays(), orderItem.getStartDate(), orderItem.getEndDate(), orderItem.getReturnStatus(), orderItem.getRenterConfirmedReturn(), orderItem.getOwnerConfirmedReturn(), orderItem.getRenterConfirmedReturnAt(), orderItem.getOwnerConfirmedReturnAt(), insuranceOutDTO));
            } else {
                orderItemOutDTOS.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), null, null, null, null, null, null, null, null, null));
            }
        }

        return orderItemOutDTOS;
    }

    public void addOrderItem(OrderItemInDTO orderItemInDTO) {
        Orders order = orderRepository.findOrderById(orderItemInDTO.getOrderId());
        if (order == null) {
            throw new ApiException("Order not found");
        }

        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(orderItemInDTO.getMarketPlaceItemId());
        if (item == null) {
            throw new ApiException("Market place item not found");
        }

        if (!orderItemInDTO.getType().matches("SELL|RENT")) {
            throw new ApiException("Type must be SELL or RENT only");
        }

        if (orderItemInDTO.getType().equals("RENT")) {
            if (orderItemInDTO.getRentalDays() == null || orderItemInDTO.getRentalDays() <= 0) {
                throw new ApiException("Rental days must be greater than zero for rent items");
            }
        } else {
            if (orderItemInDTO.getRentalDays() != null ||
                    orderItemInDTO.getDepositAmount() != null) {
                throw new ApiException("Sell items must not have rental fields");
            }
        }

        LocalDate startDate = null;
        LocalDate endDate = null;
        if (orderItemInDTO.getType().equals("RENT")) {
            startDate = LocalDate.now();
            endDate = startDate.plusDays(orderItemInDTO.getRentalDays());
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setType(orderItemInDTO.getType());
        orderItem.setQuantity(orderItemInDTO.getQuantity());
        orderItem.setUnitPrice(orderItemInDTO.getUnitPrice());
        orderItem.setRentalDays(orderItemInDTO.getRentalDays());
        orderItem.setDepositAmount(orderItemInDTO.getDepositAmount());
        orderItem.setSubtotal(orderItemInDTO.getSubtotal());
        orderItem.setStartDate(startDate);
        orderItem.setEndDate(endDate);
        orderItem.setRenterConfirmedReturn(false);
        orderItem.setOwnerConfirmedReturn(false);
        orderItem.setRenterConfirmedReturnAt(null);
        orderItem.setOwnerConfirmedReturnAt(null);
        orderItem.setReturnStatus(orderItemInDTO.getType().equals("RENT") ? "NOT_RETURNED" : null);
        orderItem.setOrders(order);
        orderItem.setMarketPlaceItem(item);
        orderItemRepository.save(orderItem);
    }

    public void updateOrderItem(Integer id, OrderItemInDTO orderItemInDTO) {
        OrderItem orderItem = orderItemRepository.findOrderItemById(id);
        if (orderItem == null) {
            throw new ApiException("Order item not found");
        }

        Orders order = orderRepository.findOrderById(orderItemInDTO.getOrderId());
        if (order == null) {
            throw new ApiException("Order not found");
        }

        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(orderItemInDTO.getMarketPlaceItemId());
        if (item == null) {
            throw new ApiException("Market place item not found");
        }

        if (!orderItemInDTO.getType().matches("SELL|RENT")) {
            throw new ApiException("Type must be SELL or RENT only");
        }

        if (orderItemInDTO.getType().equals("RENT")) {
            if (orderItemInDTO.getRentalDays() == null || orderItemInDTO.getRentalDays() <= 0) {
                throw new ApiException("Rental days must be greater than zero for rent items");
            }
        } else {
            if (orderItemInDTO.getRentalDays() != null ||
                    orderItemInDTO.getDepositAmount() != null) {
                throw new ApiException("Sell items must not have rental fields");
            }
        }

        LocalDate startDate = null;
        LocalDate endDate = null;
        if (orderItemInDTO.getType().equals("RENT")) {
            startDate = LocalDate.now();
            endDate = startDate.plusDays(orderItemInDTO.getRentalDays());
        }

        orderItem.setType(orderItemInDTO.getType());
        orderItem.setQuantity(orderItemInDTO.getQuantity());
        orderItem.setUnitPrice(orderItemInDTO.getUnitPrice());
        orderItem.setRentalDays(orderItemInDTO.getRentalDays());
        orderItem.setDepositAmount(orderItemInDTO.getDepositAmount());
        orderItem.setSubtotal(orderItemInDTO.getSubtotal());
        orderItem.setStartDate(startDate);
        orderItem.setEndDate(endDate);
        orderItem.setOrders(order);
        orderItem.setMarketPlaceItem(item);
        orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(Integer id) {
        OrderItem orderItem = orderItemRepository.findOrderItemById(id);
        if (orderItem == null) {
            throw new ApiException("Order item not found");
        }

        orderItemRepository.delete(orderItem);
    }

    @Transactional
    public OrderItemOutDTO renterConfirmReturn(Integer orderItemId, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        OrderItem orderItem = orderItemRepository.findOrderItemById(orderItemId);
        if (orderItem == null) {
            throw new ApiException("Order item not found");
        }
        if (!orderItem.getType().equals("RENT")) {
            throw new ApiException("Only rent items can be returned");
        }
        if (orderItem.getOrders() == null || orderItem.getOrders().getUser() == null || !orderItem.getOrders().getUser().getId().equals(user.getId())) {
            throw new ApiException("Only the renter can confirm return");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        MarketPlaceItem item = orderItem.getMarketPlaceItem();
        if (item == null) {
            throw new ApiException("Market place item not found");
        }
        if (item.getUser() == null || item.getUser().getNeighborhood() == null) {
            throw new ApiException("Market place item owner neighborhood is required");
        }
        if (!item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Order item is outside your neighborhood");
        }
        if ("RETURNED".equals(orderItem.getReturnStatus()) || "DAMAGED".equals(orderItem.getReturnStatus())) {
            throw new ApiException("Return process is already completed");
        }
        if (Boolean.TRUE.equals(orderItem.getRenterConfirmedReturn())) {
            throw new ApiException("Renter return is already confirmed");
        }

        orderItem.setRenterConfirmedReturn(true);
        orderItem.setRenterConfirmedReturnAt(LocalDateTime.now());
        orderItem.setReturnStatus("WAITING_OWNER_CONFIRMATION");

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        String itemName = savedOrderItem.getMarketPlaceItem() == null ? null : savedOrderItem.getMarketPlaceItem().getTitle();
        Insurance insurance = savedOrderItem.getInsurance();
        InsuranceOutDTO insuranceOutDTO = null;
        if (insurance != null) {
            Integer savedOrderItemId = insurance.getOrderItem() == null ? null : insurance.getOrderItem().getId();
            insuranceOutDTO = new InsuranceOutDTO(insurance.getId(), insurance.getDepositAmount(), insurance.getRefundedAmount(), insurance.getStatus(), insurance.getHeldAt(), insurance.getRefundedAt(), insurance.getRefundTransactionId(), savedOrderItemId);
        }
        return new OrderItemOutDTO(savedOrderItem.getId(), itemName, savedOrderItem.getType(), savedOrderItem.getQuantity(), savedOrderItem.getUnitPrice(), savedOrderItem.getSubtotal(), savedOrderItem.getRentalDays(), savedOrderItem.getStartDate(), savedOrderItem.getEndDate(), savedOrderItem.getReturnStatus(), savedOrderItem.getRenterConfirmedReturn(), savedOrderItem.getOwnerConfirmedReturn(), savedOrderItem.getRenterConfirmedReturnAt(), savedOrderItem.getOwnerConfirmedReturnAt(), insuranceOutDTO);
    }

    @Transactional
    public OrderItemOutDTO ownerConfirmDamaged(Integer orderItemId, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        OrderItem orderItem = orderItemRepository.findOrderItemById(orderItemId);
        if (orderItem == null) {
            throw new ApiException("Order item not found");
        }
        if (!orderItem.getType().equals("RENT")) {
            throw new ApiException("Only rent items can be returned");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        MarketPlaceItem item = orderItem.getMarketPlaceItem();
        if (item == null) {
            throw new ApiException("Market place item not found");
        }
        if (item.getUser() == null || item.getUser().getNeighborhood() == null) {
            throw new ApiException("Market place item owner neighborhood is required");
        }
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ApiException("Only the item owner can confirm return");
        }
        if (!item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Order item is outside your neighborhood");
        }
        if ("RETURNED".equals(orderItem.getReturnStatus()) || "DAMAGED".equals(orderItem.getReturnStatus())) {
            throw new ApiException("Return process is already completed");
        }
        if (!Boolean.TRUE.equals(orderItem.getRenterConfirmedReturn())) {
            throw new ApiException("Renter must confirm return first");
        }
        if (Boolean.TRUE.equals(orderItem.getOwnerConfirmedReturn())) {
            throw new ApiException("Owner return is already confirmed");
        }

        orderItem.setOwnerConfirmedReturn(true);
        orderItem.setOwnerConfirmedReturnAt(LocalDateTime.now());
        orderItem.setReturnStatus("DAMAGED");

        Insurance insurance = orderItem.getInsurance();
        if (insurance != null) {
            insurance.setStatus("DEDUCTED");
            insurance.setRefundedAmount(0);
            insuranceRepository.save(insurance);
        }

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        String itemName = savedOrderItem.getMarketPlaceItem() == null ? null : savedOrderItem.getMarketPlaceItem().getTitle();
        Insurance savedInsurance = savedOrderItem.getInsurance();
        InsuranceOutDTO insuranceOutDTO = null;
        if (savedInsurance != null) {
            Integer savedOrderItemId = savedInsurance.getOrderItem() == null ? null : savedInsurance.getOrderItem().getId();
            insuranceOutDTO = new InsuranceOutDTO(savedInsurance.getId(), savedInsurance.getDepositAmount(), savedInsurance.getRefundedAmount(), savedInsurance.getStatus(), savedInsurance.getHeldAt(), savedInsurance.getRefundedAt(), savedInsurance.getRefundTransactionId(), savedOrderItemId);
        }
        return new OrderItemOutDTO(savedOrderItem.getId(), itemName, savedOrderItem.getType(), savedOrderItem.getQuantity(), savedOrderItem.getUnitPrice(), savedOrderItem.getSubtotal(), savedOrderItem.getRentalDays(), savedOrderItem.getStartDate(), savedOrderItem.getEndDate(), savedOrderItem.getReturnStatus(), savedOrderItem.getRenterConfirmedReturn(), savedOrderItem.getOwnerConfirmedReturn(), savedOrderItem.getRenterConfirmedReturnAt(), savedOrderItem.getOwnerConfirmedReturnAt(), insuranceOutDTO);
    }

    @Transactional
    public OrderItemOutDTO ownerConfirmReturn(Integer orderItemId, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        OrderItem orderItem = orderItemRepository.findOrderItemById(orderItemId);
        if (orderItem == null) {
            throw new ApiException("Order item not found");
        }
        if (!orderItem.getType().equals("RENT")) {
            throw new ApiException("Only rent items can be returned");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        MarketPlaceItem item = orderItem.getMarketPlaceItem();
        if (item == null) {
            throw new ApiException("Market place item not found");
        }
        if (item.getUser() == null || item.getUser().getNeighborhood() == null) {
            throw new ApiException("Market place item owner neighborhood is required");
        }
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ApiException("Only the item owner can confirm return");
        }
        if (!item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Order item is outside your neighborhood");
        }
        if ("RETURNED".equals(orderItem.getReturnStatus()) || "DAMAGED".equals(orderItem.getReturnStatus())) {
            throw new ApiException("Return process is already completed");
        }
        if (!Boolean.TRUE.equals(orderItem.getRenterConfirmedReturn())) {
            throw new ApiException("Renter must confirm return first");
        }
        if (Boolean.TRUE.equals(orderItem.getOwnerConfirmedReturn())) {
            throw new ApiException("Owner return is already confirmed");
        }

        orderItem.setOwnerConfirmedReturn(true);
        orderItem.setOwnerConfirmedReturnAt(LocalDateTime.now());
        orderItem.setReturnStatus("RETURNED");

        Insurance insurance = orderItem.getInsurance();
        if (insurance != null) {
            insurance.setStatus("REFUNDED");
            insurance.setRefundedAt(LocalDateTime.now());
            insurance.setRefundedAmount(insurance.getDepositAmount());
            insuranceRepository.save(insurance);
        }

        if (item != null) {
            item.setQuantity(item.getQuantity() + orderItem.getQuantity());
            item.setStatus("AVAILABLE");
            marketPlaceItemRepository.save(item);
        }

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        String itemName = savedOrderItem.getMarketPlaceItem() == null ? null : savedOrderItem.getMarketPlaceItem().getTitle();
        Insurance savedInsurance = savedOrderItem.getInsurance();
        InsuranceOutDTO insuranceOutDTO = null;
        if (savedInsurance != null) {
            Integer savedOrderItemId = savedInsurance.getOrderItem() == null ? null : savedInsurance.getOrderItem().getId();
            insuranceOutDTO = new InsuranceOutDTO(savedInsurance.getId(), savedInsurance.getDepositAmount(), savedInsurance.getRefundedAmount(), savedInsurance.getStatus(), savedInsurance.getHeldAt(), savedInsurance.getRefundedAt(), savedInsurance.getRefundTransactionId(), savedOrderItemId);
        }
        return new OrderItemOutDTO(savedOrderItem.getId(), itemName, savedOrderItem.getType(), savedOrderItem.getQuantity(), savedOrderItem.getUnitPrice(), savedOrderItem.getSubtotal(), savedOrderItem.getRentalDays(), savedOrderItem.getStartDate(), savedOrderItem.getEndDate(), savedOrderItem.getReturnStatus(), savedOrderItem.getRenterConfirmedReturn(), savedOrderItem.getOwnerConfirmedReturn(), savedOrderItem.getRenterConfirmedReturnAt(), savedOrderItem.getOwnerConfirmedReturnAt(), insuranceOutDTO);
    }
}
