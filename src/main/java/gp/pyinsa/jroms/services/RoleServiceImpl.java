package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.repositories.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        List<Role> allRoles = roleRepository.findAll();
        allRoles.removeIf(role -> role.getId().equals((short) 1));
        return allRoles;
    }

    @Override
    public List<Role> getRolesByIds(List<Integer> ids) {
        return roleRepository.findByIdIn(ids);
    }

    @Override
    public Role getRoleById(Short id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

}
