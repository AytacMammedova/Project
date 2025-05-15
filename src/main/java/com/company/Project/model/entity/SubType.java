package com.company.Project.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "sub_type")
public class SubType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id")
    private ProductType productType;


    @OneToMany(mappedBy = "subType",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Product>products;
}
