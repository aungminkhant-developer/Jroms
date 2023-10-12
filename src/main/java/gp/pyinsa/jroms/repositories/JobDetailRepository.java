package gp.pyinsa.jroms.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.Team;

public interface JobDetailRepository extends JpaRepository<JobDetail, Long> {

    Page<JobDetail> findAll(Specification<JobDetail> spec, Pageable pageable);

    List<JobDetail> findAllByTeam_Id(String id);

    long countByCreatedDateBetween(Date start, Date end);

    long countByCreatedDate(Date start);

    List<JobDetail> findByJobTitleInAndJobLevelIn(List<JobTitle> jobTitle, List<JobLevel> jobLevel);

    boolean existsByJobTitleAndJobLevel(JobTitle jobTitle, JobLevel jobLevel);

    List<JobDetail> findByCreatedDateBetween(Date fromDate, Date toDate);

    List<JobDetail> findTop50ByExpireDateGreaterThan(Date date);

    List<JobDetail> findByTeamDepartmentAndCreatedDateBetween(Department department, Date fromDate, Date toDate);

    List<JobDetail> findByTeamAndCreatedDateBetween(Team team, Date fromDate, Date toDate);
}
