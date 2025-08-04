package com.company.Project.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter

public class ProductBucketId implements Serializable {
    private Long bucketId;
    private Long bucketProductId;

    public ProductBucketId() {

    }

    public ProductBucketId(Long bucketId, Long bucketProductId) {
        this.bucketId = bucketId;
        this.bucketProductId = bucketProductId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProductBucketId that = (ProductBucketId) object;
        return Objects.equals(bucketId, that.bucketId) && Objects.equals(bucketProductId, that.bucketProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucketId, bucketProductId);
    }
}
