package gp.pyinsa.jroms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gp.pyinsa.jroms.models.InterviewSchedule;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule,Long>{
    
}
