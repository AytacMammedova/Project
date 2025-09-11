package com.company.Project.service;

import com.company.Project.model.dto.request.SizeMeasurementDto;
import com.company.Project.model.dto.response.SizeRecommendationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SizeGuideService {
    public SizeRecommendationDto findMySize(Long productId,SizeMeasurementDto measurementDto);
    List<String> getHowToMeasure(String jewelryType);
}
