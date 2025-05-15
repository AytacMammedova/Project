package com.company.Project.model.dto.request;

import com.company.Project.model.Gender;
import lombok.Data;

@Data
public class ProductAddDto {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String color;
    private Gender gender;
    private String image;
}
