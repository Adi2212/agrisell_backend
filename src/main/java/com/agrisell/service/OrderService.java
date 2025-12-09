package com.agrisell.service;

import com.agrisell.dto.OrderRequest;
import com.agrisell.dto.OrderResponse;
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

import java.util.List;
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
}
