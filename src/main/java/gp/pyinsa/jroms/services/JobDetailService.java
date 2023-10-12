package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;

public interface JobDetailService {

    String updateStatus(String status);    

    JobDetail getById(Long id);

    boolean existsById(Long id);

    List<JobDetail> findAllJobDetails();

    void addNewJobDetail(JobDetail newJobDetail);

    void updateJobDetail(JobDetail updateJobDetail);

    void deleteById(Long id);

    String updateJobExpireDate(JobDetail jobDetail);

    List<JobDetail> getJobDetailsByTeamId(String teamId);

    long getJobOfferCountByCreatedDateBetween(String year, String month);

    Page<JobDetail> getFilteredJobs(String searchQuery, String location, Long position, String status,
            Pageable pageable);

    Page<JobDetail> getFilteredJobsForMng(String searchQuery, String location, Long position, String status,
            String department, String team, Pageable pageable);

    List<JobDetail> getJobDetailByJobTitlesAndJobLevels(List<JobTitle> jobTitles, List<JobLevel> jobLevels);

}
