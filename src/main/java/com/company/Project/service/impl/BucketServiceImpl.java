package com.company.Project.service.impl;

import com.company.Project.exceptions.BucketNotFoundException;
import com.company.Project.exceptions.ProductNotFoundException;
import com.company.Project.mapper.BucketMapper;
import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.entity.Bucket;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductBucket;
import com.company.Project.model.entity.User;
import com.company.Project.repository.BucketRepository;
import com.company.Project.repository.ProductBucketRepository;
import com.company.Project.repository.ProductRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class BucketServiceImpl implements BucketService {
    private final BucketRepository bucketRepository;
    private final BucketMapper bucketMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductBucketRepository productBucketRepository;

    @Override
    public BucketDto getById(Long bucketId) {
        return bucketMapper.toBucketDto(bucketRepository.findById(bucketId).orElseThrow(()->new BucketNotFoundException("No bucket with id"+bucketId)));
    }

    @Override
    public BucketDto createBucket(Integer userId) {
        User user= userRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No user with id:"+userId));

        Bucket bucket=new Bucket();
        bucket.setOrderDate(LocalDate.now());
        bucket.setAmount(0);
        bucket.setUser(user);
        return bucketMapper.toBucketDto(bucketRepository.save(bucket));

    }

    @Override
    public void addProductToBucket(Long bucketId, Integer productId, Integer quantity) {
        Bucket bucket=bucketRepository.findById(bucketId).orElseThrow(()->new BucketNotFoundException("No bucket with id: "+bucketId));
        Product product=productRepository.findById(productId).orElseThrow(()->new ProductNotFoundException("No product with id: "+productId));
        ProductBucket productBucket=new ProductBucket();
        productBucket.setBucket(bucket);
        productBucket.setProduct(product);
        productBucket.setQuantity(quantity);
        productBucket.setTotalAmount(product.getPrice()*quantity);
        productBucketRepository.save(productBucket);
        totalAmount(bucketId);

    }

    @Override
    public void deleteProductFromBucket(Long bucketId, Integer productId) {
        ProductBucket productBucket=productBucketRepository.findByBucketIdAndProductId(bucketId,productId).orElseThrow(()->new ProductNotFoundException("No product in bucket"));
        productBucketRepository.deleteById(productBucket.getId());
        totalAmount(bucketId);
    }

    @Override
    public void clearBucket(Long bucketId) {
        Bucket bucket=bucketRepository.findById(bucketId).orElseThrow(()->new BucketNotFoundException("No bucket with id: "+bucketId));
        bucket.setProductBucketList(null);
        totalAmount(bucketId);


    }
    public void totalAmount(Long bucketId){
        Bucket bucket=bucketRepository.findById(bucketId).orElseThrow(()->new BucketNotFoundException("No bucket with id: "+bucketId));
        double total = bucket.getProductBucketList().stream()
                .mapToDouble(ProductBucket::getTotalAmount)
                .sum();
        bucket.setAmount(total);
        bucketRepository.save(bucket);

    }
}
