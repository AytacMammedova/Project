package com.company.Project.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "size_guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jewelry_type")
    private String jewelryType;

    @Column(name = "size_name")
    private String sizeName;

    @Column(name = "measurement_min")
    private Double measurementMin;

    @Column(name = "measurement_max")
    private Double measurementMax;

    @Column(name = "unit")
    private String unit;

    @Column(name = "description")
    private String description;


    public SizeGuide(String jewelryType, String sizeName, Double min, Double max, String unit, String description) {
        this.jewelryType = jewelryType;
        this.sizeName = sizeName;
        this.measurementMin = min;
        this.measurementMax = max;
        this.unit = unit;
        this.description = description;
    }
}
