package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.Mail_Status;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.repositories.InterviewRepository;

@Service
public class InterviewServiceImpl implements InterviewService{

    @Autowired
    private InterviewRepository interviewRepository;

    @Override
    public List<Interview> getAllInterviewByCandidateId(long id) {
        return interviewRepository.findAllByCandidate_Id(id);  
    }

    @Override
    public Interview getInterviewById(long id) {
       return interviewRepository.findById(id).get();
    }

    @Override
    public void saveInterview(Interview interview) {
        interviewRepository.saveAndFlush(interview);
    }

    @Override
    public boolean existsByCandidateAndMailStatus(Candidate candidate, Mail_Status status) {
        return interviewRepository.existsByCandidateAndMailStatus(candidate, status);
    }

    @Override
    public List<Interview> getAllInterviews() {
        List<Interview> interviews=interviewRepository.findAll();
        return interviews;
    }

    @Override
    public List<Interview> getAllInterviewByJobDetailId(long id) {
         return interviewRepository.findAllByJobDetail_Id(id);
    }

    @Override
    public boolean existsByCandidateAndInterviewStage(Candidate candidate, InterviewStage stage) {
        return interviewRepository.existsByCandidateAndInterviewStage(candidate, stage);
    }

    @Override
    public boolean existsByCandidateAndResult(Candidate candidate, Interview_Result result) {
      return interviewRepository.existsByCandidateAndResult(candidate, result);
    }

    @Override
    public Interview getInterviewByCandidateAndMailStatus(Candidate candidate, Mail_Status mail_Status) {
       return interviewRepository.findByCandidateAndMailStatus(candidate, mail_Status);
    }

    @Override
    public void deleteInterviewById(long id) {
       interviewRepository.deleteById(id);
    }
    
}
