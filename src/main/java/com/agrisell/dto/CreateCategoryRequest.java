package com.agrisell.dto;

import lombok.Data;

@Data
public class CreateCategoryRequest {
    private String name;
    private String imageUrl;
    private Long parentId; // null = main | NOT null = sub category
}
