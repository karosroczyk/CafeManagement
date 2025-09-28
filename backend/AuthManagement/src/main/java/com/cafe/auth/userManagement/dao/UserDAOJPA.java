package com.cafe.auth.userManagement.dao;

import com.cafe.auth.userManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDAOJPA extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String username);
}
