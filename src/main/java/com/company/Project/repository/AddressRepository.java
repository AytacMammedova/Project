package com.company.Project.repository;

import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {
    @Query(value = "SELECT * FROM address a JOIN user_address ua ON a.id = ua.address_id WHERE ua.user_id = :userId", nativeQuery = true)
    List<Address>getAddressesByUserId(Long userId);

}
