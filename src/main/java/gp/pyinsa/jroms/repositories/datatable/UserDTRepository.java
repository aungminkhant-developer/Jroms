package gp.pyinsa.jroms.repositories.datatable;

import javax.validation.Valid;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.models.User;

@Repository
public interface UserDTRepository extends DataTablesRepository<User, String> {
    DataTablesOutput<User> findAllByStatus(@Valid DataTablesInput input);
}
