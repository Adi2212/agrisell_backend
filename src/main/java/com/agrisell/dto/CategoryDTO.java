package com.agrisell.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private boolean active=true;
}
