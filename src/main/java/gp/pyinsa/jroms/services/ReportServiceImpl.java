package gp.pyinsa.jroms.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.repositories.CandidateRepository;
import gp.pyinsa.jroms.repositories.InterviewRepository;
import gp.pyinsa.jroms.repositories.JobDetailRepository;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private JobDetailRepository jobDetailRepository;

    @Override
    public List<Candidate> findCandidatesByCreatedDateBetween(Date fromDate, Date toDate) {
        return candidateRepository.findByCreatedDateBetween(fromDate, toDate);
    }

    @Override
    public List<Interview> findInterviewsByCandidate(Candidate candidate) {
        return interviewRepository.findByCandidate(candidate);
    }

    @Override
    public List<Candidate> findCandidatesByDepartmentAndDateBetween(Department department, Date fromDate, Date toDate) {
        List<JobDetail> jobDetails = jobDetailRepository.findByTeamDepartmentAndCreatedDateBetween(department, fromDate, toDate);
        List<Candidate> candidates = new ArrayList<>();
        for (JobDetail jobDetail : jobDetails) {
            List<Candidate> cands = candidateRepository.findByJobDetail(jobDetail);
            candidates.addAll(cands);
        }
        return candidates;
    }

    @Override
    public List<Candidate> findCandidatesByTeamAndDateBetween(Team team, Date fromDate, Date toDate) {
        List<JobDetail> jobDetails = jobDetailRepository.findByTeamAndCreatedDateBetween(team, fromDate, toDate);
        List<Candidate> candidates = new ArrayList<>();
        for (JobDetail jobDetail : jobDetails) {
            List<Candidate> cands = candidateRepository.findByJobDetail(jobDetail);
            candidates.addAll(cands);
        }
        return candidates;
    }

    @Override
    public List<Candidate> findCandidatesByJobOffer(Long jobOfferId) {
        return candidateRepository.findAllByJobDetailId(jobOfferId);
    }
    
}
