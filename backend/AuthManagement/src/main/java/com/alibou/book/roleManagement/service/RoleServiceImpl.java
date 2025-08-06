package com.alibou.book.roleManagement.service;

import com.alibou.book.exception.DatabaseUniqueValidationException;
import com.alibou.book.exception.ResourceNotFoundException;
import com.alibou.book.roleManagement.dao.RoleDAOJPA;
import com.alibou.book.roleManagement.entity.Role;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class RoleServiceImpl implements RoleService{

    private final RoleDAOJPA roleDAOJPA;

    public RoleServiceImpl(RoleDAOJPA roleDAOJPA){ this.roleDAOJPA = roleDAOJPA; }

    @Override
    public PaginatedResponse<Role> getAllRoles(int page, int size, String[] sortBy, String[] direction){
        List<Sort.Order> orders = IntStream.range(0, sortBy.length)
                .mapToObj(i -> new Sort.Order(Sort.Direction.fromString(direction[i]), sortBy[i]))
                .toList();
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Role> rolePage = this.roleDAOJPA.findAll(pageable);

        return new PaginatedResponse<>(
                rolePage.getContent(),
                rolePage.getNumber(),
                rolePage.getTotalPages(),
                rolePage.getTotalElements(),
                rolePage.getSize());
    }

    @Override
    public Role getRoleById(Integer id){
        return this.roleDAOJPA.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id: " + id + " not found."));
    }

    @Override
    public Role createRole(Role role){
        try {
            return this.roleDAOJPA.save(role);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    public Role updateRole(Integer id, Role role){
        Role foundRole = getRoleById(id);
        foundRole.setName(role.getName());

        try {
            return this.roleDAOJPA.save(foundRole);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    public void deleteRole(Integer id){
        getRoleById(id);
        try {
            this.roleDAOJPA.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }
}
