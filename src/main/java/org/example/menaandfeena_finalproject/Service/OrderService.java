package org.example.menaandfeena_finalproject.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.OrderPaymentRequestDTO;
import org.example.menaandfeena_finalproject.DTO.Out.CheckoutResponseDTO;
import org.example.menaandfeena_finalproject.DTO.Out.InsuranceOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MoyasarChargeOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.OrderItemOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.OrderOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.OrderPaymentStatusOutDTO;
import org.example.menaandfeena_finalproject.Model.Cart;
import org.example.menaandfeena_finalproject.Model.CartItem;
import org.example.menaandfeena_finalproject.Model.Insurance;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.OrderItem;
import org.example.menaandfeena_finalproject.Model.Orders;
import org.example.menaandfeena_finalproject.Model.Payment;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.CartItemRepository;
import org.example.menaandfeena_finalproject.Repository.CartRepository;
import org.example.menaandfeena_finalproject.Repository.InsuranceRepository;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.OrderItemRepository;
import org.example.menaandfeena_finalproject.Repository.OrderRepository;
import org.example.menaandfeena_finalproject.Repository.PaymentRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final InsuranceRepository insuranceRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final EmailService emailService;
    private final PaymentService paymentService;

    public List<OrderOutDTO> getAllOrders() {
        List<OrderOutDTO> orderOutDTOS = new ArrayList<>();

        for (Orders orders : orderRepository.findAll()) {
            Integer userId = orders.getUser() == null ? null : orders.getUser().getId();
            List<OrderItemOutDTO> orderItems = new ArrayList<>();

            for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(orders.getId())) {
                String itemName = orderItem.getMarketPlaceItem() == null ? null : orderItem.getMarketPlaceItem().getTitle();
                Insurance insurance = orderItem.getInsurance();
                InsuranceOutDTO insuranceOutDTO = null;
                if (insurance != null) {
                    Integer orderItemId = insurance.getOrderItem() == null ? null : insurance.getOrderItem().getId();
                    insuranceOutDTO = new InsuranceOutDTO(insurance.getId(), insurance.getDepositAmount(), insurance.getRefundedAmount(), insurance.getStatus(), insurance.getHeldAt(), insurance.getRefundedAt(), insurance.getRefundTransactionId(), orderItemId);
                }

                if (orderItem.getType().equals("RENT")) {
                    orderItems.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), orderItem.getRentalDays(), orderItem.getStartDate(), orderItem.getEndDate(), orderItem.getReturnStatus(), orderItem.getRenterConfirmedReturn(), orderItem.getOwnerConfirmedReturn(), orderItem.getRenterConfirmedReturnAt(), orderItem.getOwnerConfirmedReturnAt(), insuranceOutDTO));
                } else {
                    orderItems.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), null, null, null, null, null, null, null, null, null));
                }
            }

            orderOutDTOS.add(new OrderOutDTO(orders.getId(), orders.getInvoiceNumber(), orders.getOrderDate(), orders.getStatus(), orders.getTotalAmount(), userId, orderItems));
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

    // Returns a single order (with its items and insurance) by id.
    public OrderOutDTO getOrderById(Integer orderId) {
        Orders order = orderRepository.findOrderById(orderId);

        if (order == null) {
            throw new ApiException("Order not found");
        }

        return buildOrderOutDTO(order);
    }

    @Transactional
    public CheckoutResponseDTO checkoutCart(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        Cart cart = cartRepository.findCartByUserId(user.getId());
        if (cart == null) {
            throw new ApiException("Cart not found");
        }

        List<CartItem> cartItems = cartItemRepository.findCartItemsByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new ApiException("Cart is empty");
        }
        for (CartItem cartItem : cartItems) {
            MarketPlaceItem item = cartItem.getMarketPlaceItem();
            if (item == null) {
                throw new ApiException("Market place item not found");
            }
            if (item.getUser() == null || item.getUser().getNeighborhood() == null) {
                throw new ApiException("Market place item owner neighborhood is required");
            }
            if (!item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
                throw new ApiException("Cart contains an item outside your neighborhood");
            }
        }

        // Validate every cart item and calculate the total (same formula as before).
        Integer totalAmount = 0;
        for (CartItem cartItem : cartItems) {
            MarketPlaceItem item = cartItem.getMarketPlaceItem();
            if (item == null) {
                throw new ApiException("Market place item not found");
            }
            if (!item.getStatus().equals("AVAILABLE")) {
                throw new ApiException("Item is not available");
            }
            if (item.getQuantity() == null || item.getQuantity() < cartItem.getQuantity()) {
                throw new ApiException("Item quantity is unavailable");
            }

            if (item.getType().equals("SELL")) {
                if (item.getPrice() == null || item.getPrice() <= 0) {
                    throw new ApiException("Item price is invalid");
                }
                if (cartItem.getRentalDays() != null) {
                    throw new ApiException("Rental days must be null for sell items");
                }
            } else if (item.getType().equals("RENT")) {
                if (cartItem.getRentalDays() == null || cartItem.getRentalDays() <= 0) {
                    throw new ApiException("Rental days must be greater than zero for rent items");
                }
                if (item.getRentPrice() == null || item.getRentPrice() <= 0) {
                    throw new ApiException("Item rent price is invalid");
                }
                if (item.getDepositAmount() == null || item.getDepositAmount() < 0) {
                    throw new ApiException("Item deposit amount is invalid");
                }
            } else {
                throw new ApiException("Type must be SELL or RENT only");
            }

            totalAmount += calculateSubtotal(item, cartItem.getQuantity(), cartItem.getRentalDays());
        }

        // Order starts as PENDING_PAYMENT and is only confirmed after Moyasar reports the payment as paid.
        Orders order = new Orders();
        order.setInvoiceNumber("INV-" + System.currentTimeMillis());
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING_PAYMENT");
        order.setTotalAmount(totalAmount);
        order.setUser(user);
        Orders savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            MarketPlaceItem item = cartItem.getMarketPlaceItem();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(savedOrder);
            orderItem.setMarketPlaceItem(item);
            orderItem.setType(item.getType());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setRenterConfirmedReturn(false);
            orderItem.setOwnerConfirmedReturn(false);

            if (item.getType().equals("SELL")) {
                orderItem.setUnitPrice(item.getPrice());
                orderItem.setRentalDays(0);
                orderItem.setDepositAmount(0);
                orderItem.setStartDate(null);
                orderItem.setEndDate(null);
                orderItem.setReturnStatus(null);
            } else {
                Integer rentalDays = cartItem.getRentalDays();
                LocalDate startDate = cartItem.getStartDate() == null ? LocalDate.now() : cartItem.getStartDate();
                LocalDate endDate = cartItem.getEndDate() == null ? startDate.plusDays(rentalDays) : cartItem.getEndDate();
                orderItem.setUnitPrice(item.getRentPrice());
                orderItem.setRentalDays(rentalDays);
                orderItem.setDepositAmount(item.getDepositAmount());
                orderItem.setStartDate(startDate);
                orderItem.setEndDate(endDate);
                orderItem.setReturnStatus("NOT_RETURNED");
            }
            orderItem.setSubtotal(calculateSubtotal(item, cartItem.getQuantity(), cartItem.getRentalDays()));

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);

            // Insurance is created up front for RENT items but stays PENDING until payment succeeds.
            if (item.getType().equals("RENT")) {
                createPendingInsurance(savedOrderItem, item);
            }

            // NOTE: item quantity is NOT reduced and the cart is NOT cleared here.
            // Both happen only after a successful card payment (see payOrderWithCard).
        }

        // Create the local payment in INITIATED state. No Moyasar call yet and no payment URL:
        // the user enters card details on our own page, which calls POST /api/v1/payment/order/{orderId}.
        String description = "Marketplace cart checkout order #" + savedOrder.getId();

        Payment payment = new Payment();
        payment.setAmount(totalAmount);
        payment.setPlatformFee(0);
        payment.setSellerAmount(totalAmount);
        payment.setStatus("INITIATED");
        // transactionId is NOT NULL in the schema; use a placeholder until Moyasar returns the real id at payment time.
        payment.setTransactionId("INIT-" + savedOrder.getId());
        payment.setDescription(description);
        payment.setOrders(savedOrder);
        Payment savedPayment = paymentRepository.save(payment);

        return new CheckoutResponseDTO(
                savedOrder.getId(),
                savedPayment.getId(),
                totalAmount,
                savedOrder.getStatus()
        );
    }

    // Processes an in-app card payment for a PENDING_PAYMENT order.
    // The amount is taken from the order (never the client) and converted to halalas for Moyasar.
    // On success: payment -> PAID, order -> PAID, stock reduced, insurance -> HELD, invoice generated + emailed.
    // On failure: payment -> FAILED, order stays PENDING_PAYMENT, nothing else changes.
    @Transactional
    public Object payOrderWithCard(Integer orderId, Integer userId, OrderPaymentRequestDTO card) {
        Orders order = orderRepository.findOrderById(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }
        validateBuyerOrderNeighborhood(order, userId);

        Payment payment = order.getPayment();
        if (payment == null) {
            throw new ApiException("Payment not found for this order");
        }

        // Idempotent: if already paid, just return the order.
        if ("PAID".equals(order.getStatus())) {
            return buildOrderOutDTO(order);
        }
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new ApiException("Order is not awaiting payment");
        }

        // Amount comes from the order only. Moyasar expects halalas (270 SAR -> 27000).
        int moyasarAmount = order.getTotalAmount() * 100;
        String description = "Marketplace order #" + order.getId();
        MoyasarChargeOutDTO charge = paymentService.chargeCard(moyasarAmount, "SAR", card, description);

        if (charge.getMoyasarPaymentId() != null) {
            payment.setTransactionId(charge.getMoyasarPaymentId());
        }
        if (charge.getTransactionUrl() != null) {
            payment.setPaymentUrl(charge.getTransactionUrl());
        }

        if ("initiated".equalsIgnoreCase(charge.getStatus())) {
            payment.setStatus("INITIATED");
            paymentRepository.save(payment);
            return new OrderPaymentStatusOutDTO(
                    order.getId(),
                    payment.getId(),
                    payment.getTransactionId(),
                    "INITIATED",
                    payment.getPaymentUrl(),
                    "Payment requires 3DS verification. Open transactionUrl, complete payment, then call POST /api/v1/payment/order/" + order.getId() + "/sync."
            );
        }

        if ("failed".equalsIgnoreCase(charge.getStatus())) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            return new OrderPaymentStatusOutDTO(
                    order.getId(),
                    payment.getId(),
                    payment.getTransactionId(),
                    "FAILED",
                    payment.getPaymentUrl(),
                    "Payment failed. Order is still PENDING_PAYMENT."
            );
        }

        if (!"paid".equalsIgnoreCase(charge.getStatus())) {
            payment.setStatus("INITIATED");
            paymentRepository.save(payment);
            return new OrderPaymentStatusOutDTO(
                    order.getId(),
                    payment.getId(),
                    payment.getTransactionId(),
                    payment.getStatus(),
                    payment.getPaymentUrl(),
                    "Payment is still pending. Moyasar status: " + charge.getStatus()
            );
        }

        return completePaidOrder(order, payment);
    }

    @Transactional
    public Object syncOrderPayment(Integer orderId, Integer userId) {
        Orders order = orderRepository.findOrderById(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }
        validateBuyerOrderNeighborhood(order, userId);

        Payment payment = order.getPayment();
        if (payment == null) {
            throw new ApiException("Payment not found for this order");
        }

        if ("PAID".equals(order.getStatus())) {
            return buildOrderOutDTO(order);
        }

        String moyasarPaymentId = payment.getTransactionId();
        if (moyasarPaymentId == null || moyasarPaymentId.startsWith("INIT-")) {
            throw new ApiException("Moyasar payment id is missing");
        }

        MoyasarChargeOutDTO charge = paymentService.fetchPayment(moyasarPaymentId);
        if (charge.getTransactionUrl() != null) {
            payment.setPaymentUrl(charge.getTransactionUrl());
        }

        if ("paid".equalsIgnoreCase(charge.getStatus())) {
            return completePaidOrder(order, payment);
        }

        if ("initiated".equalsIgnoreCase(charge.getStatus())) {
            payment.setStatus("INITIATED");
            paymentRepository.save(payment);
            return new OrderPaymentStatusOutDTO(
                    order.getId(),
                    payment.getId(),
                    payment.getTransactionId(),
                    "INITIATED",
                    payment.getPaymentUrl(),
                    "Payment is still pending 3DS completion. Open transactionUrl and complete payment, then call sync again."
            );
        }

        if ("failed".equalsIgnoreCase(charge.getStatus())) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            return new OrderPaymentStatusOutDTO(
                    order.getId(),
                    payment.getId(),
                    payment.getTransactionId(),
                    "FAILED",
                    payment.getPaymentUrl(),
                    "Payment failed. Order is still PENDING_PAYMENT."
            );
        }

        paymentRepository.save(payment);
        return new OrderPaymentStatusOutDTO(
                order.getId(),
                payment.getId(),
                payment.getTransactionId(),
                payment.getStatus(),
                payment.getPaymentUrl(),
                "Payment is not completed yet. Moyasar status: " + charge.getStatus()
        );
    }

    private OrderOutDTO completePaidOrder(Orders order, Payment payment) {
        payment.setStatus("PAID");
        order.setStatus("PAID");

        for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(order.getId())) {
            MarketPlaceItem item = orderItem.getMarketPlaceItem();
            if (item != null) {
                if (item.getQuantity() == null || item.getQuantity() < orderItem.getQuantity()) {
                    throw new ApiException("Item quantity is no longer available for: " + item.getTitle());
                }
                reduceItemQuantity(item, orderItem.getQuantity());
            }

            if ("RENT".equals(orderItem.getType())) {
                Insurance insurance = orderItem.getInsurance();
                if (insurance != null) {
                    insurance.setStatus("HELD");
                    if (insurance.getHeldAt() == null) {
                        insurance.setHeldAt(LocalDateTime.now());
                    }
                    insuranceRepository.save(insurance);
                }
            }
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        // Clear the user's cart now that the order is paid.
        Cart cart = cartRepository.findCartByUserId(order.getUser().getId());
        if (cart != null) {
            for (CartItem cartItem : cartItemRepository.findCartItemsByCartId(cart.getId())) {
                cartItemRepository.delete(cartItem);
            }
        }

        trySendInvoiceEmail(order);

        return buildOrderOutDTO(order);
    }

    private void trySendInvoiceEmail(Orders order) {
        try {
            byte[] invoicePdf = generateInvoicePdf(order.getId());
            sendInvoiceEmail(
                    order.getUser().getEmail(),
                    "Mena And Feena Invoice " + order.getInvoiceNumber(),
                    "Thank you for your order. Your invoice is attached.",
                    invoicePdf,
                    "invoice-" + order.getInvoiceNumber() + ".pdf"
            );
        } catch (Exception e) {
            log.error("Invoice email failed for paid order id {}. Payment/order updates were kept.", order.getId(), e);
        }
    }

    private void validateBuyerOrderNeighborhood(Orders order, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
            throw new ApiException("Order does not belong to this user");
        }
        validateOrderItemsInNeighborhood(order, user);
    }

    private void validateOrderItemsInNeighborhood(Orders order, User user) {
        for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(order.getId())) {
            MarketPlaceItem item = orderItem.getMarketPlaceItem();
            if (item == null) {
                throw new ApiException("Market place item not found");
            }
            if (item.getUser() == null || item.getUser().getNeighborhood() == null) {
                throw new ApiException("Market place item owner neighborhood is required");
            }
            if (!item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
                throw new ApiException("Order contains an item outside your neighborhood");
            }
        }
    }

    // ----- Small private helpers shared by the marketplace flow -----

    // Subtotal for one line: SELL = price * qty, RENT = (rentPrice * rentalDays + deposit) * qty.
    private Integer calculateSubtotal(MarketPlaceItem item, Integer quantity, Integer rentalDays) {
        if (item.getType().equals("RENT")) {
            return ((item.getRentPrice() * rentalDays) + item.getDepositAmount()) * quantity;
        }
        if (!item.getType().equals("SELL")) {
            throw new ApiException("Type must be SELL or RENT only");
        }
        return item.getPrice() * quantity;
    }

    // Creates the insurance row for a RENT order item in PENDING state (activated to HELD after payment).
    private Insurance createPendingInsurance(OrderItem orderItem, MarketPlaceItem item) {
        Insurance insurance = new Insurance();
        insurance.setDepositAmount(item.getDepositAmount());
        insurance.setRefundedAmount(0);
        insurance.setStatus("PENDING");
        insurance.setHeldAt(null);
        insurance.setOrderItem(orderItem);
        Insurance savedInsurance = insuranceRepository.save(insurance);
        orderItem.setInsurance(savedInsurance);
        return savedInsurance;
    }

    // Reduces item stock after a successful payment and flips status to SOLD/RENTED when it hits zero.
    private void reduceItemQuantity(MarketPlaceItem item, Integer quantity) {
        item.setQuantity(item.getQuantity() - quantity);
        if (item.getQuantity() == 0) {
            item.setStatus(item.getType().equals("SELL") ? "SOLD" : "RENTED");
        }
        marketPlaceItemRepository.save(item);
    }

    // Builds an OrderOutDTO (with its order items and insurance) for a given order.
    private OrderOutDTO buildOrderOutDTO(Orders order) {
        Integer orderUserId = order.getUser() == null ? null : order.getUser().getId();
        List<OrderItemOutDTO> orderItems = new ArrayList<>();
        for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(order.getId())) {
            String itemName = orderItem.getMarketPlaceItem() == null ? null : orderItem.getMarketPlaceItem().getTitle();
            Insurance insurance = orderItem.getInsurance();
            InsuranceOutDTO insuranceOutDTO = null;
            if (insurance != null) {
                Integer orderItemId = insurance.getOrderItem() == null ? null : insurance.getOrderItem().getId();
                insuranceOutDTO = new InsuranceOutDTO(insurance.getId(), insurance.getDepositAmount(), insurance.getRefundedAmount(), insurance.getStatus(), insurance.getHeldAt(), insurance.getRefundedAt(), insurance.getRefundTransactionId(), orderItemId);
            }
            if (orderItem.getType().equals("RENT")) {
                orderItems.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), orderItem.getRentalDays(), orderItem.getStartDate(), orderItem.getEndDate(), orderItem.getReturnStatus(), orderItem.getRenterConfirmedReturn(), orderItem.getOwnerConfirmedReturn(), orderItem.getRenterConfirmedReturnAt(), orderItem.getOwnerConfirmedReturnAt(), insuranceOutDTO));
            } else {
                orderItems.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), null, null, null, null, null, null, null, null, null));
            }
        }
        return new OrderOutDTO(order.getId(), order.getInvoiceNumber(), order.getOrderDate(), order.getStatus(), order.getTotalAmount(), orderUserId, orderItems);
    }

    public byte[] generateInvoicePdf(Integer orderId) {
        Orders order = orderRepository.findOrderById(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
            builder.useUnicodeBidiReorderer(new ICUBidiReorderer());
            builder.useFont(() -> {
                try {
                    return new ClassPathResource("fonts/tahoma.ttf").getInputStream();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, "InvoiceArabic");
            builder.withHtmlContent(buildInvoiceHtml(order), null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Could not generate invoice PDF");
        }
    }

    private String buildInvoiceHtml(Orders order) throws Exception {
        String template = StreamUtils.copyToString(
                new ClassPathResource("templates/invoice-template.html").getInputStream(),
                StandardCharsets.UTF_8
        );
        byte[] logoBytes = StreamUtils.copyToByteArray(
                new ClassPathResource("static/invoice/mena-feena-logo.jpeg").getInputStream()
        );
        String logoSrc = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(logoBytes);
        User buyer = order.getUser();
        Payment payment = order.getPayment();

        return template
                .replace("{{logoSrc}}", logoSrc)
                .replace("{{invoiceNumber}}", html(order.getInvoiceNumber()))
                .replace("{{paymentStatus}}", html(payment == null ? null : payment.getStatus()))
                .replace("{{orderDate}}", html(order.getOrderDate() == null ? null : order.getOrderDate().toString()))
                .replace("{{buyerName}}", html(buyer == null ? null : buyer.getFullName()))
                .replace("{{itemsRows}}", buildInvoiceItemRows(order))
                .replace("{{totalAmount}}", html(formatAmount(order.getTotalAmount())));
    }

    private String buildInvoiceItemRows(Orders order) {
        StringBuilder rows = new StringBuilder();
        for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(order.getId())) {
            MarketPlaceItem item = orderItem.getMarketPlaceItem();
            String type = orderItem.getType();

            rows.append("<tr>");
            rows.append("<td class=\"item\">");
            rows.append("<div>").append(html(item == null ? null : item.getTitle())).append("</div>");
            rows.append("<div style=\"font-size:12px;color:#9a9a9a;line-height:1.8;margin-top:4px;\">");
            rows.append("الكمية: ").append(html(orderItem.getQuantity()));
            rows.append(" | سعر الوحدة: ").append(html(formatAmount(orderItem.getUnitPrice())));

            if ("RENT".equals(type)) {
                Insurance insurance = orderItem.getInsurance();
                rows.append("<br />أيام الإيجار: ").append(html(orderItem.getRentalDays()));
                rows.append(" | تاريخ البداية: ").append(html(orderItem.getStartDate()));
                rows.append(" | تاريخ النهاية: ").append(html(orderItem.getEndDate()));
                rows.append("<br />مبلغ التأمين: ").append(html(formatAmount(orderItem.getDepositAmount())));
                rows.append(" | حالة التأمين: ").append(html(insurance == null ? null : insurance.getStatus()));
            }

            rows.append("</div>");
            rows.append("</td>");
            rows.append("<td class=\"type\">").append(html(type)).append("</td>");
            rows.append("<td class=\"amount\">").append(html(formatAmount(orderItem.getSubtotal()))).append("</td>");
            rows.append("</tr>");
        }
        return rows.toString();
    }

    private String html(Object value) {
        if (value == null) {
            return "N/A";
        }
        return HtmlUtils.htmlEscape(value.toString(), StandardCharsets.UTF_8.name());
    }

    private String formatAmount(Integer amount) {
        if (amount == null) {
            return "N/A";
        }
        return amount + " SAR";
    }

    public void sendInvoiceEmail(String to, String subject, String body, byte[] pdfBytes, String fileName) {
        emailService.sendEmailWithAttachment(to, subject, body, pdfBytes, fileName);
    }

    public byte[] generateInvoicePdf(Integer orderId, Integer userId) {
        Orders order = orderRepository.findOrderById(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }
        validateBuyerOrderNeighborhood(order, userId);
        return generateInvoicePdf(orderId);
    }

    public List<OrderOutDTO> getMyOrders(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<OrderOutDTO> orderOutDTOS = new ArrayList<>();
        for (Orders orders : orderRepository.findOrdersByUserId(userId)) {
            validateOrderItemsInNeighborhood(orders, user);
            Integer orderUserId = orders.getUser() == null ? null : orders.getUser().getId();
            List<OrderItemOutDTO> orderItems = new ArrayList<>();
            for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(orders.getId())) {
                String itemName = orderItem.getMarketPlaceItem() == null ? null : orderItem.getMarketPlaceItem().getTitle();
                Insurance insurance = orderItem.getInsurance();
                InsuranceOutDTO insuranceOutDTO = null;
                if (insurance != null) {
                    Integer orderItemId = insurance.getOrderItem() == null ? null : insurance.getOrderItem().getId();
                    insuranceOutDTO = new InsuranceOutDTO(insurance.getId(), insurance.getDepositAmount(), insurance.getRefundedAmount(), insurance.getStatus(), insurance.getHeldAt(), insurance.getRefundedAt(), insurance.getRefundTransactionId(), orderItemId);
                }
                if (orderItem.getType().equals("RENT")) {
                    orderItems.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), orderItem.getRentalDays(), orderItem.getStartDate(), orderItem.getEndDate(), orderItem.getReturnStatus(), orderItem.getRenterConfirmedReturn(), orderItem.getOwnerConfirmedReturn(), orderItem.getRenterConfirmedReturnAt(), orderItem.getOwnerConfirmedReturnAt(), insuranceOutDTO));
                } else {
                    orderItems.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), null, null, null, null, null, null, null, null, null));
                }
            }
            orderOutDTOS.add(new OrderOutDTO(orders.getId(), orders.getInvoiceNumber(), orders.getOrderDate(), orders.getStatus(), orders.getTotalAmount(), orderUserId, orderItems));
        }

        return orderOutDTOS;
    }

    public List<OrderOutDTO> getMySales(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<OrderOutDTO> orderOutDTOS = new ArrayList<>();
        for (Orders orders : orderRepository.findDistinctByOrderItemsMarketPlaceItemUserId(userId)) {
            validateOrderItemsInNeighborhood(orders, user);
            Integer orderUserId = orders.getUser() == null ? null : orders.getUser().getId();
            List<OrderItemOutDTO> orderItems = new ArrayList<>();
            for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(orders.getId())) {
                String itemName = orderItem.getMarketPlaceItem() == null ? null : orderItem.getMarketPlaceItem().getTitle();
                Insurance insurance = orderItem.getInsurance();
                InsuranceOutDTO insuranceOutDTO = null;
                if (insurance != null) {
                    Integer orderItemId = insurance.getOrderItem() == null ? null : insurance.getOrderItem().getId();
                    insuranceOutDTO = new InsuranceOutDTO(insurance.getId(), insurance.getDepositAmount(), insurance.getRefundedAmount(), insurance.getStatus(), insurance.getHeldAt(), insurance.getRefundedAt(), insurance.getRefundTransactionId(), orderItemId);
                }
                if (orderItem.getType().equals("RENT")) {
                    orderItems.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), orderItem.getRentalDays(), orderItem.getStartDate(), orderItem.getEndDate(), orderItem.getReturnStatus(), orderItem.getRenterConfirmedReturn(), orderItem.getOwnerConfirmedReturn(), orderItem.getRenterConfirmedReturnAt(), orderItem.getOwnerConfirmedReturnAt(), insuranceOutDTO));
                } else {
                    orderItems.add(new OrderItemOutDTO(orderItem.getId(), itemName, orderItem.getType(), orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal(), null, null, null, null, null, null, null, null, null));
                }
            }
            orderOutDTOS.add(new OrderOutDTO(orders.getId(), orders.getInvoiceNumber(), orders.getOrderDate(), orders.getStatus(), orders.getTotalAmount(), orderUserId, orderItems));
        }

        return orderOutDTOS;
    }

    @Transactional
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
        for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(order.getId())) {
            if (orderItem.getType().equals("RENT") && !Boolean.TRUE.equals(orderItem.getOwnerConfirmedReturn()) && orderItem.getMarketPlaceItem() != null) {
                MarketPlaceItem item = orderItem.getMarketPlaceItem();
                item.setQuantity(item.getQuantity() + orderItem.getQuantity());
                item.setStatus("AVAILABLE");
                marketPlaceItemRepository.save(item);
            }
        }

        orderRepository.save(order);
    }

    @Transactional
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
        for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(order.getId())) {
            if (orderItem.getMarketPlaceItem() != null && !Boolean.TRUE.equals(orderItem.getOwnerConfirmedReturn())) {
                MarketPlaceItem item = orderItem.getMarketPlaceItem();
                item.setQuantity(item.getQuantity() + orderItem.getQuantity());
                item.setStatus("AVAILABLE");
                marketPlaceItemRepository.save(item);
            }
        }

        orderRepository.save(order);
    }
}
