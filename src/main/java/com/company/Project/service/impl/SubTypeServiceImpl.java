package com.company.Project.service.impl;

import com.company.Project.model.entity.SubType;
import com.company.Project.repository.SubTypeRepository;
import com.company.Project.service.SubTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubTypeServiceImpl implements SubTypeService {
    private final SubTypeRepository subTypeRepository;
    @Override
    public List<SubType> getSubTypeList() {
        log.info("List of SubTypes");
        return subTypeRepository.findAll();
    }

    @Override
    public SubType getById(Integer id) {
        return subTypeRepository.findById(id).orElseThrow(IllegalStateException::new);
    }

    @Override
    public SubType add(SubType subType) {
        return subTypeRepository.save(subType);
    }

    @Override
    public SubType update(Integer id, SubType subType) {
        SubType subType1=subTypeRepository.findById(id).orElseThrow(IllegalStateException::new);
        subType1.setName(subType.getName());
        return subTypeRepository.save(subType1);
    }

    @Override
    public void delete(Integer id) {
        log.info("SubType deleted");
        subTypeRepository.deleteById(id);

    }
}
