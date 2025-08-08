package com.company.Project.service;

import com.company.Project.model.dto.SubTypeDto;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.SubType;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SubTypeService {
    List<SubTypeDto> getSubTypeList();
    SubTypeDto getById(Integer id);
    SubTypeDto add(SubType subType);
    SubTypeDto update(Integer id,SubType subType);
    void delete(Integer id);
}
