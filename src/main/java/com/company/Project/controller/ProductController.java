package com.company.Project.controller;

import com.company.Project.model.Gender;
import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductSearchCriteria;
import com.company.Project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @GetMapping
    public List<ProductDto> getProductsList(){
        return productService.getProductsList();
    }
    @GetMapping("/search")
    public Page<ProductDto> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Integer subtypeId,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            Pageable pageable){
            ProductSearchCriteria criteria = new ProductSearchCriteria();
            criteria.setName(name);
            criteria.setMinPrice(minPrice);
            criteria.setMaxPrice(maxPrice);
            criteria.setGender(gender);
            criteria.setColor(color);
            criteria.setSubtypeId(subtypeId);
            criteria.setInStock(inStock);
            criteria.setSortBy(sortBy);
            criteria.setSortDirection(sortDirection);

            return productService.searchProducts(criteria, pageable);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Integer id){
        return productService.getById(id);
    }
    @GetMapping("/subtypeId/{subtypeId}")
    public List<ProductDto> getProductsBySubtypeId( @PathVariable Integer subtypeId){
        return productService.getProductsBySubtypeId(subtypeId);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto add(@RequestBody ProductAddDto productAddDto){
        return productService.add(productAddDto);
    }
    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Integer id,@RequestBody ProductAddDto productAddDto){
        return productService.update(id, productAddDto);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id){
        productService.delete(id);
    }
}
