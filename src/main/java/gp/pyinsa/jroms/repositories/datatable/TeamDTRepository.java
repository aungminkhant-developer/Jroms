package gp.pyinsa.jroms.repositories.datatable;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.models.Team;

@Repository
public interface TeamDTRepository extends DataTablesRepository<Team, String> {
    
}
