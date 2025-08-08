package com.company.Project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bucket")
public class Bucket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bucket_no")
    private String bucketNo;
    @Column(name="order_date")
    private LocalDate orderDate;
    private double amount;


    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @JsonIgnore
    private Payment payment;

    @OneToMany(mappedBy = "bucket",cascade = CascadeType.ALL)
    private List<ProductBucket>productBucketList=new ArrayList<>();
    //total amount sum ile etmek
}
