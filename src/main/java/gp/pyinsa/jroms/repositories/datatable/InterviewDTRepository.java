package gp.pyinsa.jroms.repositories.datatable;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.models.Interview;

@Repository
public interface InterviewDTRepository extends DataTablesRepository<Interview,Long>{
    
}
