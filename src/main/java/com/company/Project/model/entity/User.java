package com.company.Project.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name field is required")
    private String name;

//    @NotBlank(message = "Username field is required")
//    private String username;

    @NotBlank(message = "Email field is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Phone field is required")
    @Pattern(
            regexp = "^\\+?994(10|50|51|55|70|77)\\d{7}$",
            message = "Invalid Azerbaijan mobile number"
    )
    private String phone;

    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth field is required")
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    @JsonBackReference
    private Role role;

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(name = "user_address",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "adress_id"))
    @JsonManagedReference
    private List<Address>addresses;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Bucket bucket;

    @Size(min = 6,message = "Password must be at least 6 characters")
    private String password;
//    private boolean isAccountNonExpired;
//    private boolean isAccountNonLocked;
//    private boolean isCredentialsNonExpired;
//    private boolean isEnabled;
//
//    private String issueToken;
//
//    @ToString.Exclude
//    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//    private List<Authority>authorities;




}
