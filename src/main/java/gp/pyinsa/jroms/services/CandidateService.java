package gp.pyinsa.jroms.services;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.CurriculumVitae;

import gp.pyinsa.jroms.models.JobDetail;

public interface CandidateService {

    List<CurriculumVitae> getFilteredCurriculumVitaeById(List<Long> candidateIds);

    List<Candidate> getAllCandidate();

    DataTablesOutput<Candidate> getAllCandidates(@Valid DataTablesInput input);

    Page<Candidate> getCandidates(int currentPage);

    Candidate getById(String id);

    List<Candidate> searchByIdOrName(String id, String name);

    String save(Candidate candidate);

    String update(Candidate candidate);

    void deleteById(String id);

    List<Candidate> getAllCandidateByJobDetailId(long id);

    List<Candidate> getAllCandidateCountByJobDetailId(Long jobId);

    List<Candidate> getCandidatesByInterviewResult(Interview_Result result);

    List<Candidate> getCandidatesByJobDetail_IdAndInterviewResult(Long jobDetailId, Interview_Result result);

    Candidate getCandidateById(long id);

    long getCountByCreatedDateBetween(String year, String month);

    long getCountByFinalResultAndCreatedDate(Interview_Result result, String year, String month);

    List<Candidate> getCandidatesByInterviewResultAndOfferMailSent(Interview_Result result,boolean status);

    List<Candidate> getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(Long jobDetailId, Interview_Result result,boolean status);

    long getCountByFinalResultAndOfferMailSendAndCreatedDate(Interview_Result result,boolean status,String year,String month);

    long getCountByJobAcceptedAndCreatedDate(boolean status,String year,String month);

    long getCountByJobAccepted(boolean status);

    long getCountByJobDetail_IdAndJobAccepted(JobDetail jobDetail,boolean status);
}
