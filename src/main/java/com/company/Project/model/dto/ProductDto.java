package com.company.Project.model.dto;

import com.company.Project.model.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductDto {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String color;
    private Gender gender;
    private String image;
    private LocalDate updatedDate;
    @JsonIgnore
    private Integer subTypeId;
}
