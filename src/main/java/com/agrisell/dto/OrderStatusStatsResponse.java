package com.agrisell.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusStatsResponse {
    private String date; // "2024-06-01"
    private Long pending;
    private Long paid;
    private Long shipped;
    private Long cancelled;
}
