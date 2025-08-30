package com.company.Project.model.dto;

import com.company.Project.model.entity.Bucket;
import com.company.Project.model.entity.Product;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class ProductBucketDto {

    private Long bucketProductId;
    private Integer quantity;
    private double totalAmount;
    private String sizeName;
    private ProductDto product;
}
