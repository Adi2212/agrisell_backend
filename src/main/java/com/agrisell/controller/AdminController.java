package com.agrisell.controller;

import com.agrisell.dto.*;
import com.agrisell.model.*;
import com.agrisell.service.AdminService;
import com.agrisell.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/farmers")
    public List<FarmerAdminResponse> getAllFarmers() {
        return adminService.getFarmers();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/farmers/{id}/status")
    public String toggleFarmer(@PathVariable Long id) {
        adminService.toggleFarmerStatus(id);
        return "Farmer status updated";
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/buyers")
    public List<BuyerAdminResponse> getAllBuyers() {
        return adminService.getBuyers();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/buyers/{id}/status")
    public String blockBuyer(@PathVariable Long id) {
        adminService.blockBuyer(id);
        return "Buyer blocked";
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/product")
    public List<ProductAdminResponse> getAllProducts() {
        return adminService.getProducts();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/products/{id}/approve")
    public String approveProduct(@PathVariable Long id) {
        adminService.approveProduct(id);
        return "Product approved";
    }


    @DeleteMapping("/products/{id}")
    public String deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return "Product deleted";
    }

    // -------------------------
    // ORDERS
    // -------------------------
    @GetMapping("/orders")
    public List<OrderAdminResponse> getAllOrders() {
        return adminService.getOrders();
    }

    @GetMapping("/orders/{id}")
    public OrderDetailsAdminResponse getOrderDetails(@PathVariable Long id) {
        return adminService.getOrderDetails(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/orders/{id}/status/{status}")
    public String updateOrderStatus(@PathVariable Long id, @PathVariable String status) {
        adminService.updateOrderStatus(id, status);
        return "Order status updated";
    }

    @GetMapping("/orders/stats/{days}")
    public List<OrderStatusStatsResponse> getOrderStats(@PathVariable Long days) {
        return orderService.getOrderStatusStats(days);
    }

}

