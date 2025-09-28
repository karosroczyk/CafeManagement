package com.cafe.auth.unit.roleManagement.service;

import com.cafe.auth.exception.ResourceNotFoundException;
import com.cafe.auth.roleManagement.dao.RoleDAOJPA;
import com.cafe.auth.roleManagement.entity.Role;
import com.cafe.auth.roleManagement.service.PaginatedResponse;
import com.cafe.auth.roleManagement.service.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceTest {
    @Mock
    private RoleDAOJPA roleDAOJPA;
    @InjectMocks
    private RoleServiceImpl roleService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetAllRoles() {
        Role item1 = new Role();
        item1.setId(1);
        Role item2 = new Role();
        item2.setId(2);
        String[] sortingFields = {"id", "name"};
        String[] directions = {"asc", "desc"};

        List<Sort.Order> orders = Arrays.asList(
                new Sort.Order(Sort.Direction.ASC, "id"),
                new Sort.Order(Sort.Direction.DESC, "name")
        );
        Pageable pageable = PageRequest.of(0, 2, Sort.by(orders));
        Page<Role> rolePage = new PageImpl<>(Arrays.asList(item1, item2));

        when(roleDAOJPA.findAll(pageable)).thenReturn(rolePage);
        PaginatedResponse<Role> result = roleService.getAllRoles(0, 2, sortingFields, directions);

        assertEquals(2, result.getSize());
        assertEquals(1, result.getData().get(0).getId());
        verify(roleDAOJPA, times(1)).findAll(pageable);
    }

    @Test
    void testGetRoleById_Correct() {
        Role item = new Role();
        item.setId(1);

        when(roleDAOJPA.findById(1)).thenReturn(Optional.of(item));
        Role result = roleService.getRoleById(1);

        assertEquals(1, result.getId());
        verify(roleDAOJPA, times(1)).findById(1);
    }

    @Test
    void testGetRoleById_IdNotFound() {
        when(roleDAOJPA.findById(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.getRoleById(100)
        );

        assertEquals("Role with id: 100 not found.", exception.getMessage());
        verify(roleDAOJPA, times(1)).findById(100);
    }
    @Test
    void testCreateRole() {
        Role item = new Role();
        item.setId(1);

        when(roleDAOJPA.save(item)).thenReturn(item);
        Role result = roleService.createRole(item);

        assertEquals(1, result.getId());
        verify(roleDAOJPA, times(1)).save(item);
    }

    @Test
    void testUpdateRole_Correct() {
        Role existingItem = new Role();
        existingItem.setId(1);
        existingItem.setName("USER");
        Role updatedItem = new Role();
        updatedItem.setId(1);
        updatedItem.setName("ADMIN");

        when(roleDAOJPA.findById(1)).thenReturn(Optional.of(existingItem));
        when(roleDAOJPA.save(any(Role.class))).thenReturn(updatedItem);
        Role result = roleService.updateRole(1, updatedItem);

        assertEquals(1, result.getId());
        assertEquals("ADMIN", result.getName());
        verify(roleDAOJPA, times(1)).findById(1);
    }

    @Test
    void testDeleteRole_Correct() {
        Role item = new Role();

        when(roleDAOJPA.findById(1)).thenReturn(Optional.of(item));
        doNothing().when(roleDAOJPA).deleteById(1);

        roleService.deleteRole(1);

        verify(roleDAOJPA, times(1)).findById(1);
        verify(roleDAOJPA, times(1)).deleteById(1);
    }

    @Test
    void testDeleteRole_IdNotFound() {
        when(roleDAOJPA.findById(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.deleteRole(100)
        );

        assertEquals("Role with id: 100 not found.", exception.getMessage());
        verify(roleDAOJPA, times(1)).findById(100);
    }
}
