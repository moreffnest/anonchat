package com.moreffnest.anonchat.services;

import com.moreffnest.anonchat.dto.PasswordDTO;
import com.moreffnest.anonchat.dto.UserDTO;
import com.moreffnest.anonchat.models.User;
import com.moreffnest.anonchat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseGet(
                () -> userRepository.findByEmail(username).orElseThrow(
                        () -> new UsernameNotFoundException("There is no user '%s'".formatted(username)))
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getUserRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }

    public void createNewUser(UserDTO userInfo) {
        User user = new User(userInfo.getUsername(),
                userInfo.getEmail(),
                passwordEncoder.encode(userInfo.getPassword()));
        user.getUserRoles().add(roleService.findByName("ROLE_USER").get());
        userRepository.save(user);
        log.info("New user {} has been created", userInfo.getUsername());
    }

    public void changeUser(String username, User changeInfo) {
        User user = userRepository.findByUsername(username).get(); //username has been received from principal
        if (changeInfo.getUsername() != null && !changeInfo.getUsername().isBlank())
            user.setUsername(changeInfo.getUsername());
        if (changeInfo.getEmail() != null && !changeInfo.getEmail().isBlank())
            user.setEmail(changeInfo.getEmail());
        if (changeInfo.getFirstName() != null && !changeInfo.getFirstName().isBlank())
            user.setFirstName(changeInfo.getFirstName());
        if (changeInfo.getLastName() != null && !changeInfo.getLastName().isBlank())
            user.setLastName(changeInfo.getLastName());
        if (changeInfo.getDateOfBirth() != null)
            user.setDateOfBirth(changeInfo.getDateOfBirth());
        user.setPreserveConversations(changeInfo.isPreserveConversations());
        userRepository.save(user);
        log.info("User {} has changed his account information", username);
    }

    public boolean changePassword(String username, PasswordDTO passwords) {
        User user = userRepository.findByUsername(username).get(); //username has been received from principal
        if (passwordEncoder.matches(passwords.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
            userRepository.save(user);
            log.info("User {} has changed his password", username);
            return true;
        }

        return false;
    }


    public void save(User user) {
        userRepository.save(user);
    }

}
