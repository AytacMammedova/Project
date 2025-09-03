package com.company.Project.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderHistoryDto {
    private Long id;
    private String orderNo;
    private LocalDate orderDate;
    private Double amount;
    private Long paymentId;
    private List<OrderProductDto> products;
    private LocalDateTime createdAt;
}
