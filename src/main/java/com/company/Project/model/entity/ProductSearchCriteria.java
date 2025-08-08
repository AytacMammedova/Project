package com.company.Project.model.entity;

import com.company.Project.model.Gender;
import lombok.Data;

@Data
public class ProductSearchCriteria {
    private String name;
    private Double minPrice;
    private Double maxPrice;
    private Gender gender;
    private String color;
    private Integer subtypeId;
    private Boolean inStock;

    private String sortBy;
    private String sortDirection;
}
