package com.company.Project.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_bucket")
public class ProductBucket {
    @EmbeddedId
    private ProductBucketId id;
    private Integer quantity;
    @Column(name="total_amount")
    private double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @MapsId("bucketId")
    private Bucket bucket;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;

}
