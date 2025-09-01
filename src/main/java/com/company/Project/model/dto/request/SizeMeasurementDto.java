package com.company.Project.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SizeMeasurementDto {
    @NotBlank(message = "Jewelry type is required")
    private String jewelryType;

    @Positive(message = "Measurement must be positive")
    private Double measurement;
}
