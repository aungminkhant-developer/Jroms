package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.Mail_Status;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.InterviewStage;

public interface InterviewService {

    List<Interview> getAllInterviewByCandidateId(long id);

    List<Interview> getAllInterviewByJobDetailId(long id);

    Interview getInterviewById(long id);

    void saveInterview(Interview interview);

    boolean existsByCandidateAndMailStatus(Candidate candidate,Mail_Status status);

    boolean existsByCandidateAndInterviewStage(Candidate candidate,InterviewStage stage);

    boolean existsByCandidateAndResult(Candidate candidate,Interview_Result result);

    List<Interview> getAllInterviews();

    Interview getInterviewByCandidateAndMailStatus(Candidate candidate,Mail_Status mail_Status);

    void deleteInterviewById(long id);

    
}
