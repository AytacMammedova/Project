package com.company.Project.model.dto.response;

import com.company.Project.model.dto.ProductSizeDto;
import lombok.Data;
import java.util.List;

@Data
public class SizeRecommendationDto {
    private String recommendedSize;
    private String message;
    private List<ProductSizeDto> availableSizes;
}