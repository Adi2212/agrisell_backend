package com.agrisell.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategoryDTO {

    private Long id;
    private String name;
    private String imageUrl;

    // minimal parent details
    private ParentDTO parent;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentDTO {
        private Long id;
        private String name;
    }
}
