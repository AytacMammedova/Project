package com.company.Project.repository;

import com.company.Project.model.entity.SizeGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SizeGuideRepository extends JpaRepository<SizeGuide, Long> {
    List<SizeGuide> findByJewelryType(String jewelryType);
}
