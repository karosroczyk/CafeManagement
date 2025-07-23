package com.alibou.book.userManagement.service;

import com.alibou.book.userManagement.dao.UserDAOJPA;
import com.alibou.book.userManagement.entity.User;
import com.alibou.book.exception.DatabaseUniqueValidationException;
import com.alibou.book.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class UserServiceImpl implements UserService {
    private final UserDAOJPA userDAOJPA;

    public UserServiceImpl(UserDAOJPA userDAOJPA){
        this.userDAOJPA = userDAOJPA;
    }

    @Override
    public PaginatedResponse<User> getAllUsers(int page, int size, String[] sortBy, String[] direction){
        List<Sort.Order> orders = IntStream.range(0, sortBy.length)
                .mapToObj(i -> new Sort.Order(Sort.Direction.fromString(direction[i]), sortBy[i]))
                .toList();
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<User> userPage = this.userDAOJPA.findAll(pageable);

        return new PaginatedResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getTotalPages(),
                userPage.getTotalElements(),
                userPage.getSize());
    }

    @Override
    public User getUserById(Integer id){
        return this.userDAOJPA.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
    }

    @Override
    public User getUserByEmail(String email){
        return this.userDAOJPA.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found."));
    }

    @Override
    public User createUser(User user){
        try {
            return this.userDAOJPA.save(user);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    @Transactional
    public User updateUser(Integer id, User user){
        User foundUser = getUserById(id);

        foundUser.setFirst_name(user.getFirst_name());
        foundUser.setLast_name(user.getLast_name());
        foundUser.setDateOfBirth(user.getDateOfBirth());
        foundUser.setEmail(user.getEmail());
        foundUser.setPassword(user.getPassword());

        try {
            return this.userDAOJPA.save(foundUser);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Integer id){
        getUserById(id);
        try {
            this.userDAOJPA.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }
}
