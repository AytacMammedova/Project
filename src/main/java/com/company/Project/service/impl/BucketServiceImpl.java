package com.company.Project.service.impl;

import com.company.Project.exceptions.*;
import com.company.Project.mapper.BucketMapper;
import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.request.BucketAddDto;
import com.company.Project.model.entity.*;
import com.company.Project.repository.BucketRepository;
import com.company.Project.repository.ProductBucketRepository;
import com.company.Project.repository.ProductRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.BucketService;
import jakarta.transaction.Transactional;
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
        log.info("Getting bucket for user ID: {}", userId);
        Bucket bucket = bucketRepository.findBucketByUserId(userId)
                    .orElseThrow(() -> new BucketNotFoundException("No bucket found for user ID: " + userId));
        log.info("Found bucket with ID: {} for user: {}", bucket.getId(), userId);
        return bucketMapper.toBucketDto(bucket);

    }

    @Override
    public BucketDto addProductToBucket(BucketAddDto bucketAddDto) {
        log.info("Adding item to bucket for user ID: {}", bucketAddDto.getUserId());
        User user= userRepository.findById(bucketAddDto.getUserId())
                .orElseThrow(()->new UserNotFoundException("No user with id:"+bucketAddDto.getUserId()));
        Product product=productRepository.findById(bucketAddDto.getProductId())
                .orElseThrow(()->new ProductNotFoundException("No product with id: "+bucketAddDto.getProductId()));
        if (product.getStock() < bucketAddDto.getQuantity()) {
            throw new OutOfStockException("Insufficient stock. Available: " + product.getStock() + ", Requested: " + bucketAddDto.getQuantity());
        }
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

        Optional<ProductBucket> existingProductBucket = productBucketRepository.findByBucketIdAndProductId(bucket.getId(), product.getId());
        if (existingProductBucket.isPresent()) {
            ProductBucket productBucket = existingProductBucket.get();
            int newQuantity = productBucket.getQuantity() + bucketAddDto.getQuantity();

            if (product.getStock() < newQuantity) {
                throw new OutOfStockException("Insufficient stock for total quantity. Available: " + product.getStock() + ", Total requested: " + newQuantity);
            }

            productBucket.setQuantity(newQuantity);
            productBucket.setTotalAmount(product.getPrice() * newQuantity);
            productBucketRepository.save(productBucket);
        } else {
            Integer maxSequence = productBucketRepository.findMaxSequenceByBucketId(bucket.getId());
            Integer nextSequence = (maxSequence == null) ? 1 : maxSequence + 1;
            ProductBucket productBucket=new ProductBucket();
            productBucket.setBucketSequence(nextSequence);
            productBucket.setBucket(bucket);
            productBucket.setProduct(product);
            productBucket.setQuantity(bucketAddDto.getQuantity());
            productBucket.setTotalAmount(product.getPrice()*bucketAddDto.getQuantity());
            productBucketRepository.save(productBucket);
        }
        totalAmount(bucket.getId());
        log.info("Product added to bucket successfully");
        return bucketMapper.toBucketDto(bucket);

    }

    @Override
    public BucketDto updateProductQuantity(Long bucketId, Long productId, Integer newQuantity) {
        log.info("Updating product {} quantity to {} in bucket {}", productId, newQuantity, bucketId);

        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException("No bucket with ID: " + bucketId));

        ProductBucket productBucket = productBucketRepository.findByBucketIdAndProductId(bucketId, productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in bucket"));

        Product product = productBucket.getProduct();
        if (product.getStock() < newQuantity) {
            throw new OutOfStockException("Insufficient stock. Available: " + product.getStock());
        }

        productBucket.setQuantity(newQuantity);
        productBucket.setTotalAmount(product.getPrice() * newQuantity);
        productBucketRepository.save(productBucket);

        totalAmount(bucketId);
        log.info("Product quantity updated successfully");

        return bucketMapper.toBucketDto(bucketRepository.findById(bucketId).orElseThrow());

    }

    @Override
    public void removeProductFromBucket(Long bucketId, Long productBucketId) {
        log.info("Removing product from bucket ID: {}, product bucket ID: {}", bucketId, productBucketId);
        if (!productBucketRepository.existsByBucketIdAndProductId(bucketId, productBucketId)) {
            throw new ProductNotFoundException("Product not found in bucket");
        }
        productBucketRepository.deleteById(productBucketId);
        totalAmount(bucketId);
        log.info("Product deleted from bucket ID: {}", bucketId);
    }

    @Override
    @Transactional
    public void clearBucket(Long bucketId) {
        log.info("Clearing bucket ID: {}", bucketId);
        Bucket bucket=bucketRepository.findById(bucketId).orElseThrow(()->new BucketNotFoundException("No bucket with id: "+bucketId));
        productBucketRepository.deleteByBucketId(bucketId);
        bucket.setAmount(0.0);
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
