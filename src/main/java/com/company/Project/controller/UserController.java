package com.company.Project.controller;

import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.UserAddDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import com.company.Project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    public List<UserDto> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id){
        return userService.getById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody UserAddDto userAddDto){
        return userService.add(userAddDto);
    }
    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id,@Valid @RequestBody UserUpdateRequest userUpdateRequest){
        return userService.update(id, userUpdateRequest);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        userService.delete(id);
    }
}
