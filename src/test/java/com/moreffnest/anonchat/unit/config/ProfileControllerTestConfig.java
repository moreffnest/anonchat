package com.moreffnest.anonchat.unit.config;

import com.moreffnest.anonchat.repositories.ConversationRepository;
import com.moreffnest.anonchat.repositories.TitleListRepository;
import com.moreffnest.anonchat.repositories.UserRepository;
import com.moreffnest.anonchat.services.RoleService;
import com.moreffnest.anonchat.services.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class ProfileControllerTestConfig {
    @MockBean
    public UserRepository userRepository;
    @MockBean
    public TitleListRepository titleListRepository;
    @MockBean
    public RoleService roleService;
    @MockBean
    public ConversationRepository conversationRepository;

    @Bean
    public UserService userService(PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, roleService, passwordEncoder);
    }
}
