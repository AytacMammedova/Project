package com.company.Project.controller;

import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.SubType;
import com.company.Project.service.SubTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subtypes")
@RequiredArgsConstructor
public class SubTypeController {
    private final SubTypeService subTypeService;
    @GetMapping
    public List<SubType> getSubTypeList(){
        return subTypeService.getSubTypeList();
    }
    @GetMapping("/{id}")
    public SubType getById(@PathVariable Integer id){
        return subTypeService.getById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubType add(@RequestBody SubType subType){
        return subTypeService.add(subType);
    }
    @PutMapping("/{id}")
    public SubType update(@PathVariable Integer id,@RequestBody SubType subType){
        return subTypeService.update(id, subType);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id){
        subTypeService.delete(id);
    }
}
