package com.company.Project.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "address")
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 255, message = "City name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "City can only contain letters and spaces")
    private String city;

    @NotBlank(message = "Region is required")
    @Size(min = 2, max = 255, message = "Region name must be between 2 and 50 characters")
    private String region;

    @NotBlank(message = "Street is required")
    @Size(min = 5, max = 255, message = "Street address must be between 5 and 100 characters")
    private String street;

    @Size(max = 255, message = "Address description cannot exceed 200 characters")
    @Column(name = "address_desc")
    private String addressDesc;

    @ManyToMany(mappedBy = "addresses",fetch = FetchType.LAZY)
    @JsonBackReference
    private List<User> users;



}
