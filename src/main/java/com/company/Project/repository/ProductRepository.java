package com.company.Project.repository;

import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product>getBySubTypeId(Integer subtypeId);
}
