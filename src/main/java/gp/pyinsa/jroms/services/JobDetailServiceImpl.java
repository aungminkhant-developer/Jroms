package gp.pyinsa.jroms.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Constants;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.JobDetailNotFoundException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.repositories.JobDetailRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;


@Service
public class JobDetailServiceImpl implements JobDetailService {

    @Autowired
    JobDetailRepository jobDetailRepository;

    @Override
    public JobDetail getById(Long id) {
        return jobDetailRepository.findById(id)
                .orElseThrow(() -> new JobDetailNotFoundException("Job Detail with id: " + id + " not found."));
    }

    @Override
    public List<JobDetail> findAllJobDetails() {
        return jobDetailRepository.findAll();
    }

    @Override
    public void addNewJobDetail(JobDetail newJobDetail) {
        // Escape HTML for some fields
        newJobDetail.escapeHTML();

        // Set expire date
        Calendar cal = Calendar.getInstance();
        newJobDetail.setOpenDate(cal.getTime());
        // cal.add(Calendar.DAY_OF_MONTH, Constants.JOB_OFFER_DURATION_DAYS);
        // newJobDetail.setExpireDate(cal.getTime());

        jobDetailRepository.saveAndFlush(newJobDetail);
    }

    @Override
    public void updateJobDetail(JobDetail updateJobDetail) {
        // Escape HTML for some fields
        updateJobDetail.escapeHTML();

        JobDetail oldJobDetail = jobDetailRepository.findById(updateJobDetail.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Job detail not found with id: " + updateJobDetail.getId()));
        CustomBeanUtils.copyNonNullProperties(updateJobDetail, oldJobDetail, "id");
        jobDetailRepository.saveAndFlush(oldJobDetail);
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
        jobDetailRepository.deleteById(id);
    }

    @Override
    public String updateJobExpireDate(JobDetail updateJobDetail) {
        // Escape HTML for some fields
        updateJobDetail.escapeHTML();

        Optional<JobDetail> optionalOldJobDetail = jobDetailRepository.findById(updateJobDetail.getId());
        if (optionalOldJobDetail.isPresent()) {
            JobDetail oldJobDetail = optionalOldJobDetail.get();
            CustomBeanUtils.copyNonNullProperties(updateJobDetail, oldJobDetail, "id");
            jobDetailRepository.saveAndFlush(oldJobDetail);
            return "Job detail updated successfully.";
        } else {
            return null;
        }
    }

    public List<JobDetail> getJobDetailsByTeamId(String teamId) {
        return jobDetailRepository.findAllByTeam_Id(teamId);
    }

    @Override
    public long getJobOfferCountByCreatedDateBetween(String year, String month) {
        if (Integer.valueOf(month) < 10) {
            month = "0" + month;
        }
        String date1 = year + "-" + month + "-01"+" 00:00:00";
        String date2=year + "-" + month + "-31"+" 23:59:59";
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1);
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("End dAte =>>"+endDate);
        long count1=jobDetailRepository.countByCreatedDateBetween(startDate, endDate);
        return count1;
    }

    @Override
    public Page<JobDetail> getFilteredJobs(String searchQuery, String location, Long position, String status,
            Pageable pageable) {
        // Create a specification based on the filters
        Specification<JobDetail> spec = Specification
        .where((root, query, cb) -> cb.equal(root.get("status"), Status.ACTIVE));
        if (searchQuery != null && !searchQuery.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                // Join the JobTitle entity
                Join<JobDetail, JobTitle> jobTitleJoin = root.join("jobTitle");
                // Join the JobLevel entity
                Join<JobDetail, JobLevel> jobLevelJoin = root.join("jobLevel", JoinType.LEFT);
                // Join the Location entity
                // Join<JobDetail, Location> locationJoin = root.join("location");

                // Define expressions for job title and job level names
                Expression<String> jobTitleNameExpression = jobTitleJoin.get("name");
                Expression<String> jobLevelNameExpression = cb.coalesce(jobLevelJoin.get("name"), cb.literal(""));

                // Create a single predicate for jobTitle.name + jobLevel.name
                Predicate titleAndLevelPredicate = cb.or(
                        cb.like(cb.concat(cb.concat(jobTitleNameExpression, " "), jobLevelNameExpression),
                                "%" + searchQuery + "%"),
                        cb.like(cb.concat(cb.concat(jobLevelNameExpression, " "), jobTitleNameExpression),
                                "%" + searchQuery + "%"));

                // Define predicates for individual fields
                Predicate titleNamePredicate = cb.like(jobTitleNameExpression, "%" + searchQuery + "%");
                Predicate levelNamePredicate = cb.like(jobLevelNameExpression, "%" + searchQuery + "%");
                Predicate salaryPredicate = cb.like(root.get("salary"), "%" + searchQuery + "%");

                // Combine all the predicates with OR condition
                return cb.or(titleAndLevelPredicate, titleNamePredicate, levelNamePredicate, salaryPredicate);
            });
        }
        if (location != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("location").get("township"), location));
        }
        if (position != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("jobTitle").get("id"), position));
        }

        if (status != null) {
            if (status.equals("OPEN")) {
                spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expireDate"), new Date()));
            } else if (status.equals("CLOSED")) {
                spec = spec.and((root, query, cb) -> cb.lessThan(root.get("expireDate"), new Date()));
            }
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                 Sort.by("createdDate").descending());

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());

        return jobDetailRepository.findAll(spec, pageable);
    }

    @Override
    public Page<JobDetail> getFilteredJobsForMng(String searchQuery, String location, Long position, String status,
            String departmentId, String teamId, Pageable pageable) {
                Specification<JobDetail> spec = Specification
                .where((root, query, cb) -> cb.equal(root.get("status"), Status.ACTIVE));
        if (searchQuery != null && !searchQuery.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                // Join the JobTitle entity
                Join<JobDetail, JobTitle> jobTitleJoin = root.join("jobTitle");
                // Join the JobLevel entity
                Join<JobDetail, JobLevel> jobLevelJoin = root.join("jobLevel", JoinType.LEFT);
                // Join the Location entity
                // Join<JobDetail, Location> locationJoin = root.join("location");
                // Join the Team entity
                Join<JobDetail, Team> teamJoin = root.join("team");
                // Join the Department entity through Team
                Join<Team, Department> departmentJoin = teamJoin.join("department");

                // Define expressions for job title and job level names
                Expression<String> jobTitleNameExpression = jobTitleJoin.get("name");
                Expression<String> jobLevelNameExpression = cb.coalesce(jobLevelJoin.get("name"), cb.literal(""));

                // Create a single predicate for jobTitle.name + jobLevel.name
                Predicate titleAndLevelPredicate = cb.or(
                        cb.like(cb.concat(cb.concat(jobTitleNameExpression, " "), jobLevelNameExpression),
                                "%" + searchQuery + "%"),
                        cb.like(cb.concat(cb.concat(jobLevelNameExpression, " "), jobTitleNameExpression),
                                "%" + searchQuery + "%"));

                // Define predicates for search
                Predicate titleNamePredicate = cb.like(jobTitleJoin.get("name"), "%" + searchQuery + "%");
                Predicate levelNamePredicate = cb.like(jobLevelJoin.get("name"), "%" + searchQuery + "%");
                Predicate salaryPredicate = cb.like(root.get("salary"), "%" + searchQuery + "%");
                // Predicate locationNamePredicate = cb.like(locationJoin.get("name"), "%" +
                // searchQuery + "%");
                Predicate departmentNamePredicate = cb.like(departmentJoin.get("name"), "%" + searchQuery + "%");
                Predicate teamNamePredicate = cb.like(teamJoin.get("name"), "%" + searchQuery + "%");

                // Combine the predicates with OR condition
                return cb.or(titleAndLevelPredicate, titleNamePredicate, levelNamePredicate, salaryPredicate,
                        departmentNamePredicate,
                        teamNamePredicate);
            });
        }

        if (location != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("location").get("township"), location));
        }

        if (position != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("jobTitle").get("id"), position));
        }

        if (departmentId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("team").get("department").get("id"), departmentId));
        }

        if (teamId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("team").get("id"), teamId));
        }

        if (status != null) {
            Date currentDate = new Date();
            if (status.equals("OPEN")) {
                spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expireDate"), currentDate));
            } else if (status.equals("CLOSED")) {
                spec = spec.and((root, query, cb) -> cb.lessThan(root.get("expireDate"), currentDate));
            }
        }

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());

        // Spec to hide jobs that has expired more than 2 months
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, Constants.JOB_OFFER_SHOWN_DAYS_AFTER_CLOSED);
        spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expireDate"), cal.getTime()));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createdDate").descending());
        return jobDetailRepository.findAll(spec, pageable);
    }

    @Override
    public List<JobDetail> getJobDetailByJobTitlesAndJobLevels(List<JobTitle> jobTitles, List<JobLevel> jobLevels) {
        return jobDetailRepository.findByJobTitleInAndJobLevelIn(jobTitles, jobLevels);
    }

    @Override
    public String updateStatus(String status) {
        // TODO Auto-generated method stub
        String message;
        try {
             Long id = Long.parseLong(status);
             try {
             JobDetail jobDetail = jobDetailRepository.getById(id);
                try {
                    jobDetail.setStatus(Status.DELETED);
                    jobDetailRepository.saveAndFlush(jobDetail);
                } catch (Exception e) {
                    // TODO: handle exception
                    return message= "error deleting job offer";
                }
            } catch (Exception e) {
                // TODO: handle exception
                return message = "can't find job detail with this id";
            }
        } catch (Exception e) {
            // TODO: handle exception
            return message = "error parsing id";
        }
        return message = "offer successfully updated";
    }

    @Override
    public boolean existsById(Long id) {
        return jobDetailRepository.existsById(id);
    }

}
