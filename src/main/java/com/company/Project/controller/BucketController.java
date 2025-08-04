package com.company.Project.controller;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.request.BucketAddDto;
import com.company.Project.model.entity.ProductBucketId;
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

    @DeleteMapping("/{bucketId}/delete-product/{productBucketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductFromBucket(@Parameter(description = "Bucket ID", example = "1")
                                            @PathVariable Long bucketId,
                                        @Parameter(description = "Product sequence number in bucket (starts from 1)", example = "2")
                                            @PathVariable Long productBucketId){
        bucketService.deleteProductFromBucket(bucketId, productBucketId);
    }


    @DeleteMapping("/clear/{bucketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearBucket(@PathVariable Long bucketId){
        bucketService.clearBucket(bucketId);
    }


}
