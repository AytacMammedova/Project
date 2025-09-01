package com.company.Project.model.dto;

import lombok.Data;

@Data
public class ProductSizeDto {
    private String sizeName;
    private Integer stockQuantity;
    private Boolean isAvailable;
}