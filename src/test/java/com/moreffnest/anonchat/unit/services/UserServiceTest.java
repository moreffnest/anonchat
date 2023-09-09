package com.moreffnest.anonchat.unit.services;

import com.moreffnest.anonchat.dto.UserDTO;
import com.moreffnest.anonchat.models.Role;
import com.moreffnest.anonchat.models.User;
import com.moreffnest.anonchat.repositories.UserRepository;
import com.moreffnest.anonchat.services.RoleService;
import com.moreffnest.anonchat.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;

    @BeforeEach
    void userServiceInit() {
        userService = new UserService(userRepository, roleService, passwordEncoder);
    }

    @Test
    void changeUser() {
        User user = new User();
        user.setUsername("strelkov1337");
        user.setEmail("oldmail@rambler.ru");
        user.setLastName("Strelkov");

        User changeInfo = new User();
        changeInfo.setUsername("girkin1488");
        changeInfo.setEmail("newmail@rambler.ru");
        changeInfo.setLastName("Runov");

        when(userRepository.findByUsername("strelkov1337"))
                .thenReturn(Optional.of(user));

        userService.changeUser(user.getUsername(), changeInfo);
        assertEquals(changeInfo.getUsername(), user.getUsername());
        assertEquals(changeInfo.getEmail(), user.getEmail());
        assertEquals(changeInfo.getLastName(), user.getLastName());
        verify(userRepository).findByUsername("strelkov1337");
        verify(userRepository).save(user);
    }

    @Test
    void createNewUser() {
        UserDTO userDTO = new UserDTO("strelkov@ro.ru", "runov322", "girkin");

        when(passwordEncoder.encode(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(roleService.findByName(eq("ROLE_USER")))
                .thenReturn(Optional.of(new Role("ROLE_USER")));


        userService.createNewUser(userDTO);

        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
    }
}