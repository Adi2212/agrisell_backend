package com.agrisell.service;

import com.agrisell.dto.OrderRequest;
import com.agrisell.dto.OrderResponse;
import com.agrisell.model.*;
import com.agrisell.repository.OrderRepository;
import com.agrisell.repository.ProductRepository;
import com.agrisell.repository.UserRepository;
import com.agrisell.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public OrderResponse placeOrder(OrderRequest dto, HttpServletRequest request) {

        Order order = new Order();
        order.setPaymentMethod(dto.getPaymentMethod());

        // 🔹 Total bill calculation
        double total = dto.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        order.setTotalAmount(total);

        System.out.println(total);

        String token = jwtUtil.extractToken(request);
        long userId= jwtUtil.extractUserId(token);
        User user =userRepository.findById(userId).orElse(null);
        order.setUserId(user.getId());
        // 🔹 Address snapshot embed
        OrderAddress da = modelMapper.map(user.getAddress(), OrderAddress.class);
        order.setDeliveryAddress(da);

        // 🔹 Order items mapping
        List<OrderItem> items = dto.getItems().stream().map(i -> {
            Address pa = productRepository.findById(i.getProductId()).get().getUser().getAddress();
            if(pa == null){
                throw new RuntimeException("Seller has no address. Order cannot be processed.");
            }
            OrderItem item = new OrderItem();
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            item.setOrder(order);
            item.setPickUpAddress(modelMapper.map(pa, OrderAddress.class));
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        // Save + add history entry
        Order savedOrder = orderRepository.save(order);
        //addHistory(savedOrder, savedOrder.getStatus());
        return modelMapper.map(savedOrder, OrderResponse.class);

    }

    public Order updateStatus(Long orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order Not Found"));

        order.setStatus(status);
        addHistory(order, status);
        return orderRepository.save(order);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
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
