package com.company.Project.model.dto;

import com.company.Project.model.entity.Bucket;
import com.company.Project.model.entity.Product;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class ProductBucketDto {

    private Integer quantity;
    private double totalAmount;
    private Bucket bucket;
    private Product product;
}
