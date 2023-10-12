package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.repositories.DepartmentRepository;

@SpringBootTest
public class DepartmentServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private static User user;

    @BeforeAll
    static void setup() {
        user = new User();
        user.setId("U001");
        user.setUsername("john");
    }

    @Test
    void addNewDepartment_WhenDataIsValid_ShouldSave() {
        // Setup
        Department department = new Department("DPT01", "Department One", user, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(department.getId())).thenReturn(false);
        when(departmentRepository.existsByNameAndStatus(department.getName(), Status.ACTIVE)).thenReturn(false);
        when(departmentRepository.existsByDepartmentHead(department.getDepartmentHead())).thenReturn(false);

        // Assert
        departmentService.addNewDepartment(department);
        verify(departmentRepository, times(1)).existsById(department.getId());
        verify(departmentRepository, times(1)).existsByNameAndStatus(department.getName(), Status.ACTIVE);
        verify(departmentRepository, times(1)).existsByDepartmentHead(department.getDepartmentHead());
        verify(departmentRepository, times(1)).saveAndFlush(department);
    }

    @Test
    void addNewDepartment_WhenIdAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        Department department = new Department("DPT01", "Department One", user, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(department.getId())).thenReturn(true);
        when(departmentRepository.existsByNameAndStatus(department.getName(), Status.ACTIVE)).thenReturn(false);
        when(departmentRepository.existsByDepartmentHead(department.getDepartmentHead())).thenReturn(false);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> departmentService.addNewDepartment(department));
        verify(departmentRepository, times(1)).existsById(department.getId());
        verify(departmentRepository, times(0)).existsByNameAndStatus(department.getName(), Status.ACTIVE);
        verify(departmentRepository, times(0)).existsByDepartmentHead(department.getDepartmentHead());
        verify(departmentRepository, times(0)).saveAndFlush(department);

    }

    @Test
    void addNewDepartment_WhenNameAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        Department department = new Department("DPT01", "Department One", user, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(department.getId())).thenReturn(false);
        when(departmentRepository.existsByNameAndStatus(department.getName(), Status.ACTIVE)).thenReturn(true);
        when(departmentRepository.existsByDepartmentHead(department.getDepartmentHead())).thenReturn(false);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> departmentService.addNewDepartment(department));
        verify(departmentRepository, times(1)).existsById(department.getId());
        verify(departmentRepository, times(1)).existsByNameAndStatus(department.getName(), Status.ACTIVE);
        verify(departmentRepository, times(0)).existsByDepartmentHead(department.getDepartmentHead());
        verify(departmentRepository, times(0)).saveAndFlush(department);

    }

    @Test
    void addNewDepartment_WhenDepartmentHeadAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        Department department = new Department("DPT01", "Department One", user, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(department.getId())).thenReturn(false);
        when(departmentRepository.existsByNameAndStatus(department.getName(), Status.ACTIVE)).thenReturn(false);
        when(departmentRepository.existsByDepartmentHead(department.getDepartmentHead())).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> departmentService.addNewDepartment(department));
        verify(departmentRepository, times(1)).existsById(department.getId());
        verify(departmentRepository, times(1)).existsByNameAndStatus(department.getName(), Status.ACTIVE);
        verify(departmentRepository, times(1)).existsByDepartmentHead(department.getDepartmentHead());
        verify(departmentRepository, times(0)).saveAndFlush(department);

    }

    @Test
    void updateDepartment_WhenDataIsValid_ShouldUpdate() {
        // Setup
        Department department = new Department("DPT01", "Department One Updated", user, Status.ACTIVE);
        Department oldDepartment = new Department("DPT01", "Department One", user, Status.ACTIVE);

        // Mock
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(oldDepartment));
        when(departmentRepository.findByNameAndStatus(department.getName(), Status.ACTIVE))
                .thenReturn(Optional.empty());
        when(departmentRepository.findByDepartmentHead(department.getDepartmentHead())).thenReturn(Optional.empty());

        // Assert
        departmentService.updateDepartment(department);
        verify(departmentRepository, times(1)).findById(department.getId());
        verify(departmentRepository, times(1)).findByNameAndStatus(department.getName(), Status.ACTIVE);
        verify(departmentRepository, times(1)).findByDepartmentHead(department.getDepartmentHead());
        verify(departmentRepository, times(1)).saveAndFlush(oldDepartment);
        assertEquals(department.getName(), oldDepartment.getName());
    }

    @Test
    void updateDepartment_WhenNameAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        Department department = new Department("DPT01", "Department Two", user, Status.ACTIVE);
        Department oldDepartment = new Department("DPT01", "Department One", user, Status.ACTIVE);
        Department duplicatedDepartment = new Department("DPT02", "Department Two", user, Status.ACTIVE);

        // Mock
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(oldDepartment));
        when(departmentRepository.findByNameAndStatus(department.getName(), Status.ACTIVE))
                .thenReturn(Optional.of(duplicatedDepartment));
        when(departmentRepository.findByDepartmentHead(department.getDepartmentHead())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> departmentService.updateDepartment(department));
        verify(departmentRepository, times(1)).findByNameAndStatus(department.getName(), Status.ACTIVE);
        verify(departmentRepository, times(0)).findByDepartmentHead(department.getDepartmentHead());
        verify(departmentRepository, times(0)).findById(department.getId());
        verify(departmentRepository, times(0)).saveAndFlush(oldDepartment);
    }

    @Test
    void updateDepartment_WhenDepartmentHeadAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        Department department = new Department("DPT01", "Department Two", user, Status.ACTIVE);
        Department oldDepartment = new Department("DPT01", "Department One", user, Status.ACTIVE);
        Department duplicatedDepartment = new Department("DPT02", "Department Two", user, Status.ACTIVE);

        // Mock
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(oldDepartment));
        when(departmentRepository.findByNameAndStatus(department.getName(), Status.ACTIVE))
                .thenReturn(Optional.empty());
        when(departmentRepository.findByDepartmentHead(department.getDepartmentHead()))
                .thenReturn(Optional.of(duplicatedDepartment));

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> departmentService.updateDepartment(department));
        verify(departmentRepository, times(1)).findByNameAndStatus(department.getName(), Status.ACTIVE);
        verify(departmentRepository, times(1)).findByDepartmentHead(department.getDepartmentHead());
        verify(departmentRepository, times(0)).findById(department.getId());
        verify(departmentRepository, times(0)).saveAndFlush(oldDepartment);
    }

    @Test
    void updateDepartment_WhenOldDepartmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Setup
        Department department = new Department("DPT01", "Department Two", user, Status.ACTIVE);
        Department oldDepartment = new Department("DPT01", "Department One", user, Status.ACTIVE);

        // Mock
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.empty());
        when(departmentRepository.findByNameAndStatus(department.getName(), Status.ACTIVE))
                .thenReturn(Optional.empty());
        when(departmentRepository.findByDepartmentHead(department.getDepartmentHead())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> departmentService.updateDepartment(department));
        verify(departmentRepository, times(1)).findByNameAndStatus(department.getName(), Status.ACTIVE);
        verify(departmentRepository, times(1)).findByDepartmentHead(department.getDepartmentHead());
        verify(departmentRepository, times(1)).findById(department.getId());
        verify(departmentRepository, times(0)).saveAndFlush(oldDepartment);
    }

    @Test
    void deleteById_WhenIdIsValid_ShouldChangeStatusToDeleted() {
        // Setup
        Department department = new Department("DPT01", "Department Two", user, Status.ACTIVE);
        String departmentId = "DPT001";

        // Mock
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // Assert
        departmentService.deleteById(departmentId);
        assertEquals(Status.DELETED, department.getStatus());
        assertNull(department.getDepartmentHead());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).saveAndFlush(department);

    }

    @Test
    void deleteById_WhenIdNotExists_ShouldThrowResourceNotFoundException() {
        // Setup
        Department department = new Department("DPT01", "Department Two", user, Status.ACTIVE);
        String departmentId = "DPT02";

        // Mock
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> departmentService.deleteById(departmentId));
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(0)).saveAndFlush(department);

    }

    @Test
    void getActiveDepartments_WhenCalled_ShouldReturnActiveDepartmentsList() {
        // Setup
        Department department1 = new Department("DPT01", "Department One", user, Status.ACTIVE);
        Department department2 = new Department("DPT02", "Department Two", user, Status.ACTIVE);
        List<Department> departments = List.of(department1, department2);

        // Mock
        when(departmentRepository.findByStatus(Status.ACTIVE)).thenReturn(departments);

        // Assert
        List<Department> activeDepartments = departmentService.getActiveDepartments();
        assertEquals(departments.size(), activeDepartments.size());
        verify(departmentRepository, times(1)).findByStatus(Status.ACTIVE);
    }
}
