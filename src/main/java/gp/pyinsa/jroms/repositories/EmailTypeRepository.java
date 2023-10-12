package gp.pyinsa.jroms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.EmailType;

public interface EmailTypeRepository extends JpaRepository<EmailType,Short>{
    
    List<EmailType> findAllByStatus(Status status);

    boolean existsByNameAndStatusAndRole(String name,Status status,EmailRole role);

    EmailType findByIdAndStatus(short id,Status status);

    List<EmailType> findAllByStatusAndAndRole(Status status,EmailRole role);
}
