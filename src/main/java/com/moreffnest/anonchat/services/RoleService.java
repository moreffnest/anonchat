package com.moreffnest.anonchat.services;

import com.moreffnest.anonchat.models.Role;
import com.moreffnest.anonchat.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

}
