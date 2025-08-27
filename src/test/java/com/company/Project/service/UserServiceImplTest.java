package com.company.Project.service;

import com.company.Project.mapper.UserMapper;
import com.company.Project.model.dto.UserDto;
import com.company.Project.model.entity.User;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    private User user;
    private User user2;
    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    void setUp(){
        user=new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@example.com");
        user.setPhone("0000000000");

        user2=new User();
        user2.setId(2L);
        user2.setName("user");
        user2.setEmail("user@example.com");
        user2.setPhone("0000000000");

        userDto=new UserDto();
        userDto.setName("user");
        userDto.setPhone("0000000000");
        userDto.setEmail("user@example.com");

        userDto2=new UserDto();
        userDto2.setName("user");
        userDto2.setPhone("0000000000");
        userDto2.setEmail("user@example.com");

    }

    @AfterEach
    void tearDown(){
        user=null;
        user2=null;
        userDto=null;
        userDto2=null;
    }
    @Test
    void givenUserList(){
        List<User> userList=List.of(user,user2) ;
        List<UserDto>userDtoList=List.of(userDto,userDto2);
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toUserDtoList(userList)).thenReturn(userDtoList);

        List<UserDto> userDtoList1=user.getStudentsList();

        assertThat(studentDtoList1.getLast().getName()).isEqualTo("Vusale");
        verify(studentMapper,times(1)).toStudentDtoList(studentList);
        verify(studentRepository,times(1)).findAll();
    }
}
