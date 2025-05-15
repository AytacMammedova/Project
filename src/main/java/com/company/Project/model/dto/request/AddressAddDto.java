package com.company.Project.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressAddDto {
    private String city;
    private String region;
    private String street;
    @JsonProperty("addressDesc")
    private String addressDesc;
}
