package com.alibou.book.roleManagement.dao;

import com.alibou.book.roleManagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleDAOJPA extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String roleStudent);
}
