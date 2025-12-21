package com.agrisell.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaymentRequest {
    private Long orderId;
    private List<OrderItemRequest> items;
}
