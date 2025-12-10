package com.agrisell.service;

import com.agrisell.dto.OrderRequest;
import com.agrisell.dto.OrderResponse;
import com.agrisell.dto.OrderStatusStatsResponse;
import com.agrisell.model.*;
import com.agrisell.repository.OrderRepository;
import com.agrisell.repository.OrderStatusHistoryRepository;
import com.agrisell.repository.ProductRepository;
import com.agrisell.repository.UserRepository;
import com.agrisell.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    public OrderResponse placeOrder(OrderRequest dto, HttpServletRequest request) {

        Order order = new Order();
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus(Status.PENDING);

        // 🔹 Extract logged-in user
        String token = jwtUtil.extractToken(request);
        long userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUserId(user.getId());

        // 🔹 Snapshot of delivery address
        OrderAddress da = modelMapper.map(user.getAddress(), OrderAddress.class);
        order.setDeliveryAddress(da);

        // 🔹 Calculate total + build order items
        AtomicReference<Double> total = new AtomicReference<>(0.0);

        List<OrderItem> items = dto.getItems().stream().map(i -> {

            Product product = productRepository.findById(i.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            double price = product.getPrice(); // Fetch REAL price from DB
            double lineTotal = price * i.getQuantity();

            total.updateAndGet(v -> v + lineTotal);

            OrderItem item = new OrderItem();
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(price); // store DB price
            item.setOrder(order);

            // Seller pickup address
            Address pa = product.getUser().getAddress();
            if (pa == null) {
                throw new RuntimeException("Seller has no address.");
            }

            item.setPickUpAddress(modelMapper.map(pa, OrderAddress.class));

            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        // 🔹 Set total amount
        order.setTotalAmount(total.get());

        // 🔹 Save order
        Order savedOrder = orderRepository.save(order);

// 🔹 Save first history entry (PENDING)
        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(savedOrder.getStatus().name());
        history.setOrder(savedOrder);

        orderStatusHistoryRepository.save(history);


        return modelMapper.map(savedOrder, OrderResponse.class);
    }


    public Order updateStatus(Long orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order Not Found"));

        order.setStatus(status);
        addHistory(order, status);
        return orderRepository.save(order);
    }

    public List<OrderResponse> getUserOrders(HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        Long userId = jwtUtil.extractUserId(token);
        List<Order> orders= orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(order->modelMapper.map(order,OrderResponse.class)).collect(Collectors.toList());
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order Not Found"));
    }

    private void addHistory(Order order, Status status) {
        OrderStatusHistory entry = new OrderStatusHistory();
        entry.setStatus(status.name());
        entry.setOrder(order);
        order.getHistory().add(entry);
    }


    public List<OrderStatusStatsResponse> getOrderStatusStats(Long days) {
        // convert to LocalDateTime bounds
        LocalDateTime start = LocalDate.now().minusDays(days).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);

        List<Object[]> rows = orderRepository.countByDateAndStatus(start, end);

        // Map<LocalDate, Map<status, count>>
        Map<LocalDate, Map<String, Long>> map = new LinkedHashMap<>();

        // initialize map for each date in range with zeros
        LocalDate d = LocalDate.now().minusDays(days);
        while (!d.isAfter(LocalDate.now())) {
            Map<String, Long> inner = new HashMap<>();
            inner.put("PENDING", 0L);
            inner.put("PAID", 0L);
            inner.put("SHIPPED", 0L);
            inner.put("CANCELLED", 0L);
            map.put(d, inner);
            d = d.plusDays(1);
        }

        // populate from query rows
        for (Object[] row : rows) {
            // row[0] -> date (String or java.sql.Date), row[1] -> status, row[2] -> count
            LocalDate rowDate;
            if (row[0] instanceof java.sql.Date) {
                rowDate = ((java.sql.Date) row[0]).toLocalDate();
            } else if (row[0] instanceof String) {
                rowDate = LocalDate.parse((String) row[0]);
            } else {
                // fallback
                rowDate = LocalDate.parse(row[0].toString());
            }

            String status = (String) row[1];
            Long cnt;
            if (row[2] instanceof BigInteger) {
                cnt = ((BigInteger) row[2]).longValue();
            } else {
                cnt = Long.parseLong(row[2].toString());
            }

            Map<String, Long> inner = map.getOrDefault(rowDate, new HashMap<>());
            inner.put(status, cnt);
            map.put(rowDate, inner);
        }

        // Build response list
        List<OrderStatusStatsResponse> out = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, Long>> e : map.entrySet()) {
            LocalDate key = e.getKey();
            Map<String, Long> counts = e.getValue();
            out.add(new OrderStatusStatsResponse(
                    key.toString(),
                    counts.getOrDefault("PENDING", 0L),
                    counts.getOrDefault("PAID", 0L),
                    counts.getOrDefault("SHIPPED", 0L),
                    counts.getOrDefault("CANCELLED", 0L)
            ));
        }
        return out;
    }
}
