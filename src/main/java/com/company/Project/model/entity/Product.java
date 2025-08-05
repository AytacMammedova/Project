package com.company.Project.model.entity;

import com.company.Project.model.Gender;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;
import java.util.List;

@Data@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double price;
    @Column(name = "stock_quantity")
    private Integer stock;
    private String color;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "image_url")
    private String image;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdDate;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedDate;

    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subtype_id")
    @JsonBackReference
    private SubType subType;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductBucket>productBucketList;


}
