package com.cafe.auth.tokenManagement.dao;

import com.cafe.auth.tokenManagement.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);
}
