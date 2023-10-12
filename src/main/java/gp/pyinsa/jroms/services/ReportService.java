package gp.pyinsa.jroms.services;

import java.util.Date;
import java.util.List;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.Team;

public interface ReportService {
    List<Candidate> findCandidatesByCreatedDateBetween(Date fromDate, Date toDate);

    List<Candidate> findCandidatesByJobOffer(Long jobOfferId);

    List<Interview> findInterviewsByCandidate(Candidate candidate);

    List<Candidate> findCandidatesByDepartmentAndDateBetween(Department department, Date fromDate, Date toDate);

    List<Candidate> findCandidatesByTeamAndDateBetween(Team team, Date fromDate, Date toDate);
}
