package gp.pyinsa.jroms.repositories.datatable;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import gp.pyinsa.jroms.models.JobDetail;

public interface JobDetailDTRepository extends DataTablesRepository<JobDetail, Long> {
    
}
