package com.example.op3.services;

import com.example.op3.models.Role;
import com.example.op3.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;

public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void add(Role role) {
        roleRepository.save(role);
    }

    public void delete(Role role) {
        roleRepository.delete(role);
    }

    public Optional<Role> get(Long id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> getByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

}
