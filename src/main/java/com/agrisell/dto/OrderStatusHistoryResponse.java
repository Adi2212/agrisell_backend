package com.agrisell.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderStatusHistoryResponse {

    private Long id;
    private String status;
    private LocalDateTime changedAt;
}
