package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.Role;

public interface RoleService {
    List<Role> getAllRoles();

    List<Role> getRolesByIds(List<Integer> ids);
    
    Role getRoleById(Short byte1);
    
}
