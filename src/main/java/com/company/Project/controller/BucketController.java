package com.company.Project.controller;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buckets")
@RequiredArgsConstructor
public class BucketController {
    private final BucketService bucketService;
    @GetMapping("/{bucketId}")
    public BucketDto getById(@PathVariable Long bucketId){
        return bucketService.getById(bucketId);
    }
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BucketDto createBucket(@PathVariable Integer userId){
        return bucketService.createBucket(userId);
    }
    @PostMapping("/{bucketId}/add-product/{productId}/{quantity}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToBucket(@PathVariable Long bucketId,@PathVariable Integer productId,@PathVariable Integer quantity){
        bucketService.addProductToBucket(bucketId,productId,quantity);
    }
    @DeleteMapping("/{bucketId}/delete-product/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductFromBucket(@PathVariable Long bucketId,@PathVariable Integer productId){
        bucketService.deleteProductFromBucket(bucketId, productId);
    }
    @DeleteMapping("/clear/{bucketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearBucket(@PathVariable Long bucketId){
        bucketService.clearBucket(bucketId);
    }


}
