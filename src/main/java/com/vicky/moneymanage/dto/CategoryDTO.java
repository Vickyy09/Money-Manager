package com.vicky.moneymanage.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private Long profileId;
    private String name;
    private String type;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
