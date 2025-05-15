package com.company.Project.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "address")
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String city;
    private String region;
    private String street;
    @Column(name = "address_desc")
    private String addressDesc;

    @ManyToMany(mappedBy = "addresses")
    @JsonBackReference
    private List<User> users;



}
