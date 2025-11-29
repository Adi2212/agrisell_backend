package com.agrisell.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainCategoryDTO {

    private Long id;
    private String name;
    private String imageUrl;

    // Only subcategories (no parent here)
    //private List<SubCategoryDTO> subcategories;
}
