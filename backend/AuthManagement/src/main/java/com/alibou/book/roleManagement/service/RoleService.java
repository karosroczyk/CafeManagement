package com.alibou.book.roleManagement.service;

import com.alibou.book.roleManagement.entity.Role;
import com.alibou.book.roleManagement.service.PaginatedResponse;

public interface RoleService {
    PaginatedResponse<Role> getAllRoles(int page, int size, String[] sortBy, String[] direction);
    Role getRoleById(Integer id);
    Role createRole(Role user);
    Role updateRole(Integer id, Role user);
    void deleteRole(Integer id);
}
