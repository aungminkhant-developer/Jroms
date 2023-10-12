package gp.pyinsa.jroms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.Mail_Status;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.InterviewStage;

public interface InterviewRepository extends JpaRepository<Interview,Long>{
    List<Interview> findAllByCandidate_Id(long id);
    List<Interview> findAllByJobDetail_Id(long id);
    boolean existsByCandidateAndMailStatus(Candidate candidate,Mail_Status status);
    boolean existsByCandidateAndInterviewStage(Candidate candidate,InterviewStage stage);
    boolean existsByCandidateAndResult(Candidate candidate,Interview_Result result);
    List<Interview> findByCandidate(Candidate candidate);
    Interview findByCandidateAndMailStatus(Candidate candidate,Mail_Status mail_Status);
    Interview findByCandidateAndResult(Candidate candidate,Interview_Result result);
}
