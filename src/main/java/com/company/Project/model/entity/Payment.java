package com.company.Project.model.entity;

import com.company.Project.model.PaymentMethod;
import com.company.Project.model.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_no")
    private String paymentNo;

    private LocalDate date;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "original_bucket_id")
    private Long originalBucketId;

    @OneToOne(mappedBy = "payment",cascade = CascadeType.ALL)
    @JsonIgnore
    private Bucket bucket;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

}
