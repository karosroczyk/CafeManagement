package com.cafe.auth.roleManagement.dao;

import com.cafe.auth.roleManagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleDAOJPA extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String roleStudent);
}
