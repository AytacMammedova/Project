package com.company.Project.service.impl;

import com.company.Project.exceptions.BucketAlreadyExistsException;
import com.company.Project.exceptions.BucketNotFoundException;
import com.company.Project.exceptions.ProductNotFoundException;
import com.company.Project.exceptions.UserNotFoundException;
import com.company.Project.mapper.BucketMapper;
import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.request.BucketAddDto;
import com.company.Project.model.entity.*;
import com.company.Project.repository.BucketRepository;
import com.company.Project.repository.ProductBucketRepository;
import com.company.Project.repository.ProductRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.BucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class BucketServiceImpl implements BucketService {
    private final BucketRepository bucketRepository;
    private final BucketMapper bucketMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductBucketRepository productBucketRepository;

    @Override
    public BucketDto getById(Long userId) {
        return bucketMapper.toBucketDto(bucketRepository.findBucketByUserId(userId).orElseThrow(()->new BucketNotFoundException("No bucket with id"+userId)));
    }

    @Override
    public BucketDto addProductToBucket(BucketAddDto bucketAddDto) {
        log.info("Adding item to bucket for user ID: {}", bucketAddDto.getUserId());
        User user= userRepository.findById(bucketAddDto.getUserId()).orElseThrow(()->new UserNotFoundException("No user with id:"+bucketAddDto.getUserId()));
        Product product=productRepository.findById(bucketAddDto.getProductId()).orElseThrow(()->new ProductNotFoundException("No product with id: "+bucketAddDto.getProductId()));
        Optional<Bucket> optionalBucket=bucketRepository.findBucketByUserId(bucketAddDto.getUserId());
        Bucket bucket;
        if(optionalBucket.isPresent()) {
            bucket = optionalBucket.get();
        }else{
            bucket=new Bucket();
            bucket.setBucketNo(generateBucketNumber());
            bucket.setOrderDate(LocalDate.now());
            bucket.setAmount(0);
            bucket.setUser(user);
            bucketRepository.save(bucket);
        }

        Long maxId = productBucketRepository.findMaxBucketProductIdByBucketId(bucket.getId());
        Long nextId = (maxId == null) ? 1L : maxId + 1;

        ProductBucketId pk = new ProductBucketId();
        pk.setBucketId(bucket.getId());
        pk.setBucketProductId(nextId);

        ProductBucket productBucket=new ProductBucket();
        productBucket.setId(pk);
        productBucket.setBucket(bucket);
        productBucket.setProduct(product);
        productBucket.setQuantity(bucketAddDto.getQuantity());
        productBucket.setTotalAmount(product.getPrice()*bucketAddDto.getQuantity());
        productBucketRepository.save(productBucket);
        totalAmount(bucket.getId());
        bucketRepository.save(bucket);

        log.info("Added item to basket with ID: {}",bucket.getId());

        return bucketMapper.toBucketDto(bucket);

    }

    @Override
    public void deleteProductFromBucket(Long bucketId, Long productBucketId) {
        log.info("Deleting product from bucket ID: {}, product bucket ID: {}", bucketId, productBucketId);
        ProductBucketId compositeKey = new ProductBucketId(bucketId, productBucketId);
        if (!productBucketRepository.existsById(compositeKey)) {
            throw new ProductNotFoundException("Product not found in bucket");
        }
        productBucketRepository.deleteById(productBucketId);
        totalAmount(bucketId);
        log.info("Product deleted from bucket ID: {}", bucketId);
    }


    @Override
    public void clearBucket(Long bucketId) {
        log.info("Clearing bucket ID: {}", bucketId);
        Bucket bucket=bucketRepository.findById(bucketId).orElseThrow(()->new BucketNotFoundException("No bucket with id: "+bucketId));
        productBucketRepository.deleteByBucketId(bucketId);
        bucket.setProductBucketList(null);
        totalAmount(bucketId);
        bucketRepository.save(bucket);
        log.info("Bucket cleared: {}", bucketId);

    }

    public void totalAmount(Long bucketId){
        Bucket bucket=bucketRepository.findById(bucketId).orElseThrow(()->new BucketNotFoundException("No bucket with id: "+bucketId));
        double total = bucket.getProductBucketList().stream()
                .mapToDouble(ProductBucket::getTotalAmount)
                .sum();
        bucket.setAmount(total);
        bucketRepository.save(bucket);

    }
    private static String generateBucketNumber(){

        Random random=new Random();
        StringBuilder accountNumber=new StringBuilder();
        for(int i=0;i<15;i++){
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();

    }
}
