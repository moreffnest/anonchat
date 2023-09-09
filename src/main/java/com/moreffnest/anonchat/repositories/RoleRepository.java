package com.moreffnest.anonchat.repositories;

import com.moreffnest.anonchat.models.Role;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends ListCrudRepository<Role, Integer> {

    Optional<Role> findByName(String roleName);
}
