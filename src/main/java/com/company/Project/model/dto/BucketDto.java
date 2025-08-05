package com.company.Project.model.dto;

import com.company.Project.model.entity.Payment;
import com.company.Project.model.entity.ProductBucket;
import com.company.Project.model.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class BucketDto {


    private String bucketNo;
    private LocalDate orderDate;
    private double amount;
    private PaymentDto payment;

    private List<ProductBucketDto> productBucketList;
}
