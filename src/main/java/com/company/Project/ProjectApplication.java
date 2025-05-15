package com.company.Project;

import com.company.Project.model.entity.Category;
import com.company.Project.model.entity.ProductType;
import com.company.Project.repository.CategoryRepository;
import com.company.Project.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ProjectApplication {
	private final CategoryRepository categoryRepository;
	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}


	public void run(String... args) throws Exception {
		Category category=categoryRepository.findById(2).get();
		log.info("Category = {}",category);
	}




}
