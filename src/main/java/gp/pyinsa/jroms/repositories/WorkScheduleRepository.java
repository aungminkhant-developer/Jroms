package gp.pyinsa.jroms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.WorkSchedule;



@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Short> {
    List<WorkSchedule> findByStatus(Status status);
    
}