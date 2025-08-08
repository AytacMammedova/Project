package com.company.Project.model.dto;

import com.company.Project.model.PaymentMethod;
import com.company.Project.model.PaymentStatus;
import com.company.Project.model.entity.Bucket;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class PaymentDto {

    private String  paymentNo;
    private LocalDate date;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Long bucketId;
    private Double amount;
}
