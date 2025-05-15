package com.company.Project.model.dto;

import com.company.Project.model.entity.ProductType;
import lombok.Data;

import java.util.List;
@Data
public class CategoryDto {
    private String name;
    private List<ProductType> productTypes;
}
