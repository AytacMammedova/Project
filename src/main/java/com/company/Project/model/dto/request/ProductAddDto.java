package com.company.Project.model.dto.request;

import com.company.Project.model.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductAddDto {
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999,999.99")
    private Double price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Max(value = 999999, message = "Stock cannot exceed 999,999")
    private Integer stock;

    @Size(max = 50, message = "Color name cannot exceed 50 characters")
    private String color;

    private Gender gender;
    private String image;
    private Integer subTypeId;
}
