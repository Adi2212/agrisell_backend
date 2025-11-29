package com.agrisell.dto;

import com.agrisell.model.OrderItem;
import com.agrisell.model.Status;
import lombok.*;

import java.util.List;

@Data
@RequiredArgsConstructor
public class OrderResponse {
    private Long id;
    private Double totalAmount;
    private Status status;
}
