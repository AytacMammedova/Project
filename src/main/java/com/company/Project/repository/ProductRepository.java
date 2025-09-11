package com.company.Project.repository;

import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product>getBySubTypeId(Integer subtypeId);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
