package gp.pyinsa.jroms.repositories.datatable;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.models.Department;

@Repository
public interface DepartmentDTRepository extends DataTablesRepository<Department, String> {
    
}
