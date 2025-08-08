package com.company.Project.controller;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.request.BucketAddDto;
import com.company.Project.service.BucketService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buckets")
@RequiredArgsConstructor
public class BucketController {
    private final BucketService bucketService;
    @GetMapping("/{userId}")
    public BucketDto getById(@PathVariable Long userId){
        return bucketService.getById(userId);
    }

    @PostMapping("/add-product")
    @ResponseStatus(HttpStatus.CREATED)
    public BucketDto addProductToBucket(@RequestBody BucketAddDto bucketAddDto){
        return bucketService.addProductToBucket(bucketAddDto);
    }
    @PutMapping("/{bucketId}/products/{productId}/quantity/{quantity}")
    public BucketDto updateProductQuantity(@PathVariable Long bucketId, @PathVariable Long productId, @PathVariable Integer quantity){
        return bucketService.updateProductQuantity(bucketId, productId, quantity);
    }

    @DeleteMapping("/{bucketId}/remove-product/{productBucketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProductFromBucket(@PathVariable Long bucketId, @PathVariable Long productBucketId){
        bucketService.removeProductFromBucket(bucketId, productBucketId);
    }


    @DeleteMapping("/clear/{bucketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearBucket(@PathVariable Long bucketId){
        bucketService.clearBucket(bucketId);
    }


}
