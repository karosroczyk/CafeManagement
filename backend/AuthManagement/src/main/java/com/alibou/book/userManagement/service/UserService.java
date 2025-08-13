package com.alibou.book.userManagement.service;

import com.alibou.book.userManagement.entity.User;

import java.security.Principal;

public interface UserService {
    PaginatedResponse<User> getAllUsers(int page, int size, String[] sortBy, String[] direction);
    User getUserById(Integer id);
    User getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Integer id, User user);
    void deleteUser(Integer id);
}
