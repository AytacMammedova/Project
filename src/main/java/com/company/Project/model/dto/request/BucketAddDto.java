package com.company.Project.model.dto.request;

import lombok.Data;

@Data
public class BucketAddDto {
    private Long userId;
    private Integer productId;
    private Integer quantity;
}
