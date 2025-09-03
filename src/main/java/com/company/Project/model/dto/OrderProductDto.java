package com.company.Project.model.dto;

import lombok.Data;

@Data
public class OrderProductDto {
    private String productName;
    private Integer quantity;
    private String sizeName;
    private Double price;
    private Double totalAmount;
}
