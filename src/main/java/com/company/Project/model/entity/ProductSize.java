package com.company.Project.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_sizes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Integer productId; // References products.id

    @Column(name = "size_name", nullable = false)
    private String sizeName; // "XS", "S", "M", "L", "XL"

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity; // Stock for this specific size

    @Column(name = "is_comfort_fit")
    private Boolean isComfortFit = false;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    // Optional: Add relationship back to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
}
