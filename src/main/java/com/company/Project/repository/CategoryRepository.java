package com.company.Project.repository;

import com.company.Project.model.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
//    @EntityGraph(attributePaths = "productTypes")
//    Optional<Category> findById(Integer id);
    Optional<Category> findByName(String name);
}
