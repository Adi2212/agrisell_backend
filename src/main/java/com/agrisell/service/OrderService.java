package com.agrisell.service;

import com.agrisell.dto.*;
import com.agrisell.model.*;
import com.agrisell.repository.*;
import com.agrisell.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    // ================= CREATE ORDER =================

    public OrderResponse placeOrder(OrderRequest dto, HttpServletRequest request) {

        Order order = new Order();
        order.setStatus(Status.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        Long userId = jwtUtil.extractUserId(jwtUtil.extractToken(request));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUserId(user.getId());
        order.setDeliveryAddress(
                modelMapper.map(user.getAddress(), OrderAddress.class)
        );

        AtomicReference<Double> total = new AtomicReference<>(0.0);

        List<OrderItem> items = dto.getItems().stream().map(i -> {

            Product product = productRepository.findById(i.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(i.getQuantity());
            item.setPrice(product.getPrice());
            item.setOrder(order); // ✅ VERY IMPORTANT

            total.updateAndGet(v -> v + product.getPrice() * i.getQuantity());

            Address sellerAddress = product.getUser().getAddress();
            item.setPickUpAddress(
                    modelMapper.map(sellerAddress, OrderAddress.class)
            );

            return item;
        }).toList();

        order.setItems(items);
        order.setTotalAmount(total.get());

        Order savedOrder = orderRepository.save(order);
        addHistory(savedOrder, Status.PENDING);

        return buildOrderResponse(savedOrder);
    }

    // ================= PAYMENT FAILED =================

    public OrderResponse markPaymentFailed(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
        }

        return buildOrderResponse(order);
    }

    // ================= PAYMENT SUCCESS =================

    public OrderResponse markPaymentSuccess(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return buildOrderResponse(order);
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(Status.CONFIRMED);

        Order saved = orderRepository.save(order);
        addHistory(saved, Status.CONFIRMED);

        return buildOrderResponse(saved);
    }

    // ================= UPDATE ORDER STATUS =================

    public OrderResponse updateStatus(Long orderId, Status status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        Order saved = orderRepository.save(order);
        addHistory(saved, status);

        return buildOrderResponse(saved);
    }

    // ================= GET USER ORDERS =================

    public List<OrderResponse> getUserOrders(HttpServletRequest request) {

        Long userId = jwtUtil.extractUserId(jwtUtil.extractToken(request));

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::buildOrderResponse)
                .toList();
    }

    // ================= GET SINGLE ORDER =================

    public OrderResponse getOrder(Long id) {
        return buildOrderResponse(
                orderRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Order not found"))
        );
    }


    public List<OrderStatusStatsResponse> getOrderStatusStats(Long days) {

        LocalDateTime start = LocalDate.now()
                .minusDays(days)
                .atStartOfDay();

        LocalDateTime end = LocalDate.now()
                .atTime(LocalTime.MAX);

        List<Object[]> rows = orderRepository.countByDateAndStatus(start, end);

        // Initialize map with all dates and all statuses
        Map<LocalDate, Map<String, Long>> map = new LinkedHashMap<>();

        LocalDate d = LocalDate.now().minusDays(days);
        while (!d.isAfter(LocalDate.now())) {
            Map<String, Long> inner = new HashMap<>();
            inner.put("PENDING", 0L);
            inner.put("CONFIRMED", 0L);
            inner.put("SHIPPED", 0L);
            inner.put("DELIVERED", 0L);
            inner.put("CANCELLED", 0L);
            map.put(d, inner);
            d = d.plusDays(1);
        }

        // Fill data from DB
        for (Object[] row : rows) {

            LocalDate rowDate;
            if (row[0] instanceof java.sql.Date sqlDate) {
                rowDate = sqlDate.toLocalDate();
            } else {
                rowDate = LocalDate.parse(row[0].toString());
            }

            String status = row[1].toString();

            Long count = (row[2] instanceof BigInteger bi)
                    ? bi.longValue()
                    : Long.parseLong(row[2].toString());

            map.get(rowDate).put(status, count);
        }

        // Build response
        List<OrderStatusStatsResponse> response = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<String, Long>> entry : map.entrySet()) {
            Map<String, Long> c = entry.getValue();

            response.add(new OrderStatusStatsResponse(
                    entry.getKey().toString(),
                    c.get("PENDING"),
                    c.get("CONFIRMED"),
                    c.get("SHIPPED"),
                    c.get("DELIVERED"),
                    c.get("CANCELLED")
            ));
        }

        return response;
    }


    // ================= HELPERS =================

    private void addHistory(Order order, Status status) {
        OrderStatusHistory h = new OrderStatusHistory();
        h.setOrder(order);
        h.setStatus(status.name());
        historyRepository.save(h);
    }



    private OrderResponse buildOrderResponse(Order order) {

        OrderResponse r = new OrderResponse();
        r.setOrderId(order.getId());
        r.setTotalAmount(order.getTotalAmount());
        r.setOrderStatus(order.getStatus());
        r.setPaymentStatus(order.getPaymentStatus());
        r.setCreatedAt(order.getCreatedAt());
        r.setDeliveryAddress(
                modelMapper.map(order.getDeliveryAddress(), AddressResponse.class)
        );

        r.setItems(
                order.getItems().stream().map(item -> {
                    OrderItemResponse ir = new OrderItemResponse();
                    ir.setProductId(item.getProductId());
                    ir.setQuantity(item.getQuantity());
                    ir.setPrice(item.getPrice());
                    ir.setLineTotal(item.getPrice() * item.getQuantity());
                    ir.setProductName(
                            productRepository.findById(item.getProductId())
                                    .map(Product::getName)
                                    .orElse("Product")
                    );
                    return ir;
                }).toList()
        );

        return r;
    }
}
