package gp.pyinsa.jroms.repositories.datatable;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import gp.pyinsa.jroms.models.Location;

public interface LocationDTRepository extends DataTablesRepository<Location, Short> {
    
}
