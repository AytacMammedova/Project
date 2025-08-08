package com.company.Project.model.dto;

import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
public class SubTypeDto {
    private Integer id;
    private String name;
    private ProductTypeDto productType;

}
