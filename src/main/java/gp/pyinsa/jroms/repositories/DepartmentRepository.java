package gp.pyinsa.jroms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.User;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    boolean existsByNameAndStatus(String name, Status status);

    Optional<Department> findByNameAndStatus(String name, Status status);

    boolean existsByDepartmentHead(User departmentHead);

    Optional<Department> findByDepartmentHead(User departmentHead);

    List<Department> findByStatus(Status status);

    Optional<Department> findByDepartmentHeadId(String id);
}
