package com.company.Project.service;

import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.SubType;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SubTypeService {
    List<SubType> getSubTypeList();
    SubType getById(Integer id);
    SubType add(SubType subType);
    SubType update(Integer id,SubType subType);
    void delete(Integer id);
}
