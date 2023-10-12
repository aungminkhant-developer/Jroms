package gp.pyinsa.jroms.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findAllByJobDetail_Id(long jobDetailId);

    List<Candidate> findAllByJobDetailId(Long jobDetailId);

    long countByCreatedDateBetween(Date start, Date end);

    long countByCreatedDateBetweenAndFinalResult(Date start,Date enDate,Interview_Result result);

    List<Candidate> findAllByFinalResult(Interview_Result result);

    List<Candidate> findAllByJobDetail_IdAndFinalResult(Long id, Interview_Result result);

    List<Candidate> findByJobDetailJobTitleAndJobDetailJobLevel(JobTitle jobTitle, JobLevel jobLevel);

    List<Candidate> findByJobDetailJobTitleAndJobDetailJobLevelAndCreatedDateBetween(JobTitle jobTitle, JobLevel jobLevel, Date startDate, Date endDate);

    List<Candidate> findByJobDetail(JobDetail jobDetail);

    List<Candidate> findByCreatedDateBetween(Date fromDate, Date toDate);

    List<Candidate> findByJobDetailOrderByCreatedDateDesc(JobDetail jobDetail);

    List<Candidate> findAllByFinalResultAndJobOfferMailSent(Interview_Result result,boolean status);

    List<Candidate> findAllByJobDetail_IdAndFinalResultAndJobOfferMailSent(Long id, Interview_Result result,boolean status);

    long countByCreatedDateBetweenAndFinalResultAndJobOfferMailSent(Date start,Date endDate,Interview_Result result,boolean status);

    long countByCreatedDateBetweenAndJobAccepted(Date start,Date endDate,boolean status);

    long countByJobAccepted(boolean status);

    long countByJobDetailAndJobAccepted(JobDetail jobDetail,boolean status);
}
