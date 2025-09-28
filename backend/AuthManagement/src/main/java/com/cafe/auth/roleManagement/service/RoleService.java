package com.cafe.auth.roleManagement.service;

import com.cafe.auth.roleManagement.entity.Role;

public interface RoleService {
    PaginatedResponse<Role> getAllRoles(int page, int size, String[] sortBy, String[] direction);
    Role getRoleById(Integer id);
    Role createRole(Role user);
    Role updateRole(Integer id, Role user);
    void deleteRole(Integer id);
}
