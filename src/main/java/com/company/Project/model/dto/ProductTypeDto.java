package com.company.Project.model.dto;

import com.company.Project.model.entity.Category;
import com.company.Project.model.entity.SubType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
public class ProductTypeDto {
    private Integer id;
    private String name;
    private CategoryDto category;
}
