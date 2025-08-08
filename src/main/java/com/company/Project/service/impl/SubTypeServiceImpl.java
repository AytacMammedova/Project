package com.company.Project.service.impl;

import com.company.Project.exceptions.CategoryNotFoundException;
import com.company.Project.mapper.SubTypeMapper;
import com.company.Project.model.dto.SubTypeDto;
import com.company.Project.model.entity.SubType;
import com.company.Project.repository.SubTypeRepository;
import com.company.Project.service.SubTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubTypeServiceImpl implements SubTypeService {
    private final SubTypeRepository subTypeRepository;
    private final SubTypeMapper subTypeMapper;
    @Override
    public List<SubTypeDto> getSubTypeList() {
        log.info("Getting all SubTypes");
        return subTypeMapper.toSubTypeDtoList(subTypeRepository.findAll());
    }

    @Override
    public SubTypeDto getById(Integer id) {
        log.info("Getting SubType by id: {}", id);
        return subTypeMapper.toSubTypeDto(subTypeRepository.findById(id).orElseThrow(()-> new CategoryNotFoundException("No subType with id: "+id)));
    }

    @Override
    public SubTypeDto add(SubType subType) {

        log.info("Adding new SubType: {}", subType.getName());
        if (subType.getName() == null || subType.getName().trim().isEmpty()) {
            throw new CategoryNotFoundException("No subType wit name+ "+subType.getName());
        }
        subType.setName(subType.getName().trim());
        SubType savedSubType = subTypeRepository.save(subType);
        log.info("SubType added successfully with id: {}", savedSubType.getId());
        return subTypeMapper.toSubTypeDto(savedSubType);
    }

    @Override
    public SubTypeDto update(Integer id, SubType subType) {
        log.info("Updating SubType with id: {}", id);
        SubType existingSubType = subTypeRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("No subType with id: " + id));

        if(Objects.nonNull(subType.getName()) && !subType.getName().trim().isEmpty()){
            existingSubType.setName(subType.getName().trim());
        }

        SubType savedSubType = subTypeRepository.save(existingSubType);
        log.info("SubType updated successfully");
        return subTypeMapper.toSubTypeDto(savedSubType);
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting SubType with id: {}", id);
        if (!subTypeRepository.existsById(id)) {
            throw new CategoryNotFoundException("No SubType with id: " + id);
        }
        subTypeRepository.deleteById(id);
        log.info("SubType deleted successfully");
    }
}
