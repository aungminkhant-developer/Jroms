package gp.pyinsa.jroms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.JobLevel;

@Repository
public interface JobLevelRepository extends JpaRepository<JobLevel, Short> {
    List<JobLevel> findByStatus(Status status);

    boolean existsByNameAndStatus(String name, Status status);
}
