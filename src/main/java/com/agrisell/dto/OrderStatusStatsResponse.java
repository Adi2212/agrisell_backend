package com.agrisell.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusStatsResponse {
    private String date;
    private Long pending;
    private Long confirmed;
    private Long shipped;
    private Long delivered;
    private Long cancelled;

}
