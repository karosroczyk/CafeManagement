package com.alibou.book.roleManagement.controller;

import com.alibou.book.exception.InvalidInputException;
import com.alibou.book.roleManagement.entity.Role;
import com.alibou.book.roleManagement.service.PaginatedResponse;
import com.alibou.book.roleManagement.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) { this.roleService = roleService; }

    @GetMapping
    public ResponseEntity<PaginatedResponse<Role>> getAllRoles(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "2") int size,
        @RequestParam(defaultValue = "last_name") String[] sortBy,
        @RequestParam(defaultValue = "asc") String[] direction){
        if (page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + ", size: " + size + " provided.");

        PaginatedResponse<Role> roles = this.roleService.getAllRoles(page, size, sortBy, direction);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        Role role = this.roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role, BindingResult result){
        if (result.hasErrors()) {
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());
        }

        Role createdRole = this.roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(
            @PathVariable Integer id, @Valid @RequestBody Role role, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException("Invalid Role: " + result.getFieldError().getDefaultMessage() + " provided.");

        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        Role updatedRole = this.roleService.updateRole(id, role);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Role> deleteRole(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        this.roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
