package com.cafe.auth.userManagement.service;

import com.cafe.auth.userManagement.entity.User;

public interface UserService {
    PaginatedResponse<User> getAllUsers(int page, int size, String[] sortBy, String[] direction);
    User getUserById(Integer id);
    User getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Integer id, User user);
    void deleteUser(Integer id);
}
