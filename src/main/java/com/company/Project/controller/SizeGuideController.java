package com.company.Project.controller;

import com.company.Project.model.dto.ProductSizeDto;
import com.company.Project.model.dto.request.SizeMeasurementDto;
import com.company.Project.model.dto.response.SizeRecommendationDto;
import com.company.Project.service.ProductSizeService;
import com.company.Project.service.SizeGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/size-guide")
@RequiredArgsConstructor
public class SizeGuideController {

    private final SizeGuideService sizeGuideService;
    private final ProductSizeService productSizeService;

    @PostMapping("/product/{productId}/find-size")
    public SizeRecommendationDto findMySize(@PathVariable Integer productId,
                                            @RequestBody SizeMeasurementDto measurementDto) {
        return sizeGuideService.findMySize(productId, measurementDto);
    }

    @GetMapping("/{jewelryType}/how-to-measure")
    public List<String> howToMeasure(@PathVariable String jewelryType) {
        return sizeGuideService.getHowToMeasure(jewelryType);
    }

    @GetMapping("/product/{productId}/sizes")
    public List<ProductSizeDto> getAvailableSizes(@PathVariable Integer productId) {
        return productSizeService.getAvailableSizes(productId);
    }
}