package gp.pyinsa.jroms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.BadRequestException;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.repositories.DepartmentRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void addNewDepartment(Department department) {
        if(department.getDepartmentHead() == null) {
            throw new BadRequestException("Department head must be present.");
        }

        if (departmentRepository.existsById(department.getId())) {
            throw new ResourceAlreadyExistsException("Department id already exists.");
        }

        if (departmentRepository.existsByNameAndStatus(department.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Department name already exists.");
        }

        if (departmentRepository.existsByDepartmentHead(department.getDepartmentHead())) {
            throw new ResourceAlreadyExistsException("The assigned person already manages a department.");
        }

        departmentRepository.saveAndFlush(department);
    }

    @Override
    public void updateDepartment(Department updatedDepartment) {
        if(updatedDepartment.getDepartmentHead() == null) {
            throw new BadRequestException("Department head must be present.");
        }

        Optional<Department> deptOpt = departmentRepository.findByNameAndStatus(updatedDepartment.getName(), Status.ACTIVE);
        if (deptOpt.isPresent() && !deptOpt.get().getId().equals(updatedDepartment.getId())) {
            throw new ResourceAlreadyExistsException("Department name already exists.");
        }

        deptOpt = departmentRepository.findByDepartmentHead(updatedDepartment.getDepartmentHead());
        if (deptOpt.isPresent() && !deptOpt.get().getId().equals(updatedDepartment.getId())) {
            throw new ResourceAlreadyExistsException("The assigned person already manages a department.");
        }
        Optional<Department> oldDepartmentOpt = departmentRepository.findById(updatedDepartment.getId());
        if(oldDepartmentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Old department not found.");
        }
        Department oldDepartment = oldDepartmentOpt.get();
        CustomBeanUtils.copyNonNullProperties(updatedDepartment, oldDepartment, "id");
        departmentRepository.saveAndFlush(oldDepartment);
    }

    @Override
    public void deleteById(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found."));
        department.setStatus(Status.DELETED);
        department.setDepartmentHead(null);
        departmentRepository.saveAndFlush(department);
    }

    @Override
    public List<Department> getActiveDepartments() {
        return departmentRepository.findByStatus(Status.ACTIVE);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

}
