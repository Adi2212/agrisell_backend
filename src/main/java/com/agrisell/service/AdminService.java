package com.agrisell.service;

import com.agrisell.dto.*;
import com.agrisell.model.*;
import com.agrisell.repository.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final ModelMapper modelMapper;


    public List<FarmerAdminResponse> getFarmers() {
        return userRepository.findByRole(Role.FARMER)
                .stream()
                .map(farmer -> {
                    FarmerAdminResponse res = new FarmerAdminResponse();
                    res.setId(farmer.getId());
                    res.setName(farmer.getName());
                    res.setPhone(farmer.getPhone());
                    res.setAccStatus(farmer.getAccStatus());
                    res.setProductCount(productRepository.countByUserId(farmer.getId()));
                    return res;
                })
                .collect(Collectors.toList());
    }


    public void toggleFarmerStatus(Long id) {
        User farmer = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        System.out.println("toggleFarmerStatus: " + farmer.getAccStatus());
        AccStatus currentStatus = farmer.getAccStatus();

        if (currentStatus.equals(AccStatus.ACTIVE)) {
            // Only set to INACTIVE if currently ACTIVE
            farmer.setAccStatus(AccStatus.INACTIVE);
        } else {
            // Set to ACTIVE if currently anything else (INACTIVE, PENDING, etc.)
            farmer.setAccStatus(AccStatus.ACTIVE);
        }
        userRepository.save(farmer);
    }


    public List<BuyerAdminResponse> getBuyers() {
        return userRepository.findByRole(Role.BUYER)
                .stream()
                .map(buyer -> {
                    BuyerAdminResponse res = new BuyerAdminResponse();
                    res.setId(buyer.getId());
                    res.setName(buyer.getName());
                    res.setEmail(buyer.getEmail());

                    int orderCount = orderRepository.countByUserId(buyer.getId());
                    double totalSpent = orderRepository.sumTotalByUserId(buyer.getId());
                    res.setAccStatus(buyer.getAccStatus());
                    res.setOrderCount(orderCount);
                    res.setTotalSpent(totalSpent);

                    return res;
                })
                .collect(Collectors.toList());
    }


    public void blockBuyer(Long id) {
        User buyer = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        AccStatus currentStatus = buyer.getAccStatus();
        if (currentStatus.equals(AccStatus.ACTIVE)) {
            buyer.setAccStatus(AccStatus.INACTIVE);
        }
        else {
            buyer.setAccStatus(AccStatus.ACTIVE);
        }
        userRepository.save(buyer);
    }


    public List<ProductAdminResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    ProductAdminResponse res = new ProductAdminResponse();
                    res.setId(product.getId());
                    res.setName(product.getName());
                    res.setFarmerName(product.getUser().getName());
                    res.setPrice(product.getPrice());
                    res.setStock(product.getStockQuantity());
                    //res.setApproved(product.isApproved());
                    return res;
                })
                .collect(Collectors.toList());
    }


    public void approveProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        //product.setApproved(true);
        productRepository.save(product);
    }


    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }


    public List<OrderAdminResponse> getOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> {
                    OrderAdminResponse res = new OrderAdminResponse();
                    res.setId(order.getId());
                    res.setBuyerName(
                            userRepository.findById(order.getUserId())
                                    .map(User::getName)
                                    .orElse("Unknown")
                    );
                    res.setTotalAmount(order.getTotalAmount());
                    res.setStatus(order.getStatus().name());
                    res.setCreatedAt(order.getCreatedAt().toString());
                    return res;
                })
                .collect(Collectors.toList());
    }


    public OrderDetailsAdminResponse getOrderDetails(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderDetailsAdminResponse res = modelMapper.map(order, OrderDetailsAdminResponse.class);

        // Add buyer name
        res.setBuyerName(
                userRepository.findById(order.getUserId())
                        .map(User::getName)
                        .orElse("Unknown")
        );

        // Map each item
        res.setItems(
                order.getItems().stream()
                        .map(item -> modelMapper.map(item, OrderItemAdminResponse.class))
                        .collect(Collectors.toList())
        );

        // Add history entries
        res.setHistory(
                order.getHistory().stream()
                        .map(h -> modelMapper.map(h, OrderStatusHistoryResponse.class))
                        .collect(Collectors.toList())
        );

        return res;
    }


    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Status newStatus = Status.valueOf(status.toUpperCase());
        order.setStatus(newStatus);
        orderRepository.save(order);

        // Save history entry
        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(newStatus.name());
        history.setOrder(order);

        orderStatusHistoryRepository.save(history);
    }
}

