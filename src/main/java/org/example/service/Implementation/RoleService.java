package org.example.service.Implementation;

import org.example.Logging.LogUtils;
import org.example.domain.Role;
import org.example.repository.RoleRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleService extends BaseServiceImpl<Role, Long> {

    private final RoleRepository roleRepository;
    private static final Logger log = LogUtils.getLogger(RoleService.class);

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Create Role
    public Role createRole(Role role) {
        log.info("Attempting to create role: {}", role.getName());
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role already exists!");
        }
        Role saved = roleRepository.save(role);
        log.info("Role created successfully with id: {}", saved.getId());
        return saved;
    }

    // Get all roles
    public List<Role> getAllRoles() {
        log.debug("Fetching all roles...");
        return roleRepository.findAll();
    }

    // Get role by ID
    public Role getRoleById(Long id) {
        log.debug("Fetching role with id={}", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found!"));
    }

    // Update role
    public Role updateRole(Long id, Role updatedRole) {
        log.info("Updating role id={} with new name={}", id, updatedRole.getName());
        Role role = getRoleById(id);
        Role saved = roleRepository.save(role);
        log.info("Role updated successfully id={}", saved.getId());
        return saved;
    }

    // Delete role
    public void deleteRole(Long id) {
        log.warn("Deleting role id={}", id);
        roleRepository.deleteById(id);
    }
}