package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.Department;

public interface DepartmentService {
    List<Department> getActiveDepartments();

    List<Department> getAllDepartments();

    void addNewDepartment(Department department);

    void deleteById(String id);

    void updateDepartment(Department updatedDepartment);
}
