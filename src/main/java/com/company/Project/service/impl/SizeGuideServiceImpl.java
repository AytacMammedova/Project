package com.company.Project.service.impl;

import com.company.Project.model.dto.request.SizeMeasurementDto;
import com.company.Project.model.dto.response.SizeRecommendationDto;
import com.company.Project.service.ProductSizeService;
import com.company.Project.service.SizeGuideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SizeGuideServiceImpl implements SizeGuideService {

    private final ProductSizeService productSizeService;

    @Override
    public SizeRecommendationDto findMySize(Integer productId, SizeMeasurementDto measurementDto) {
        String recommendedSize = calculateSize(measurementDto);

        boolean inStock = productSizeService.isAvailable(productId, recommendedSize, 1);

        SizeRecommendationDto result = new SizeRecommendationDto();
        result.setRecommendedSize(recommendedSize);
        result.setAvailableSizes(productSizeService.getAvailableSizes(productId));

        if (inStock) {
            result.setMessage("Perfect! Size " + recommendedSize + " is available and in stock.");
        } else {
            result.setMessage("Your recommended size " + recommendedSize + " is out of stock. Available sizes are shown below.");
        }

        return result;
    }

    private String calculateSize(SizeMeasurementDto dto) {
        String type = dto.getJewelryType().toUpperCase();
        Double measurement = dto.getMeasurement();

        switch (type) {
            case "RINGS":
                if (measurement <= 15.0) return "XS";
                if (measurement <= 16.0) return "S";
                if (measurement <= 17.5) return "M";
                if (measurement <= 19.0) return "L";
                return "XL";

            case "BRACELETS":
                if (measurement <= 15.0) return "XS";
                if (measurement <= 16.5) return "S";
                if (measurement <= 18.0) return "M";
                if (measurement <= 19.5) return "L";
                return "XL";

            case "NECKLACES":

                if (measurement <= 32.0) return "XS";
                if (measurement <= 36.0) return "S";
                if (measurement <= 40.0) return "M";
                if (measurement <= 45.0) return "L";
                return "XL";

            default:
                return "M";
        }
    }

    @Override
    public List<String> getHowToMeasure(String jewelryType) {
        switch (jewelryType.toUpperCase()) {
            case "RINGS":
                return Arrays.asList(
                        "Measure your finger inner diameter in millimeters (mm)",
                        "Use a ring sizer or measure an existing ring"
                );
            case "BRACELETS":
                return Arrays.asList(
                        "Measure your wrist in centimeters (cm)",
                        "Add 1-2cm for comfort"
                );
            case "NECKLACES":
                return Arrays.asList(
                        "Choose your preferred length:",
                        "36cm = Choker, 42cm = Short, 50cm = Medium, 65cm = Long"
                );

            default:
                return Arrays.asList("Visit Cartier boutique for professional sizing");
        }
    }
}