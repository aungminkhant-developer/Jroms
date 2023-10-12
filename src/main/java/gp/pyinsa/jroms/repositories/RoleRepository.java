package gp.pyinsa.jroms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gp.pyinsa.jroms.models.Role;

public interface RoleRepository extends JpaRepository<Role, Short> {
    List<Role> findByIdIn(List<Integer> ids);
}
